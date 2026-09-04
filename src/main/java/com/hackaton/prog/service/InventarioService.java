package com.hackaton.prog.service;

import com.hackaton.prog.dto.AlertaDesabastoDTO;
import com.hackaton.prog.dto.ArticuloStockDTO;
import com.hackaton.prog.exception.StockInsuficienteException;
import com.hackaton.prog.model.Articulo;
import com.hackaton.prog.model.Movimiento;
import com.hackaton.prog.model.enums.TipoMovimiento;
import com.hackaton.prog.repository.ArticuloRepository;
import com.hackaton.prog.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InventarioService {

    private final MovimientoRepository movimientoRepository;
    private final ArticuloRepository articuloRepository;

    public InventarioService(MovimientoRepository movimientoRepository,
                             ArticuloRepository articuloRepository) {
        this.movimientoRepository = movimientoRepository;
        this.articuloRepository = articuloRepository;
    }

    /**
     * Obtiene el stock disponible exacto acumulado para (Centro, Campaña, Artículo).
     * Nunca devuelve null (si no hay movimientos retorna 0).
     */
    public BigDecimal obtenerStock(Integer centroId, Integer campaniaId, Integer articuloId) {
        BigDecimal stock = movimientoRepository.calcularStock(centroId, campaniaId, articuloId);
        return stock != null ? stock : BigDecimal.ZERO;
    }

    /**
     * Valida que exista stock suficiente para una operación de egreso (entrega, merma, transferencia salida o ajuste negativo).
     * Lanza StockInsuficienteException si la cantidad requerida excede las existencias.
     */
    public void validarStockSuficiente(Integer centroId, Integer campaniaId, Integer articuloId, BigDecimal cantidadRequerida) {
        if (cantidadRequerida == null || cantidadRequerida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser un número positivo mayor a cero.");
        }

        BigDecimal stockDisponible = obtenerStock(centroId, campaniaId, articuloId);
        if (stockDisponible.compareTo(cantidadRequerida) < 0) {
            throw new StockInsuficienteException(
                String.format("Stock insuficiente. Disponible: %s, Solicitado: %s", stockDisponible, cantidadRequerida),
                stockDisponible,
                cantidadRequerida
            );
        }
    }

    /**
     * Calcula el análisis predictivo de desabasto en memoria sin sobrecargar la base de datos (Burn Rate).
     * Proyecta los días de cobertura con base en las salidas de insumos (ENTREGA, TRANSFERENCIA_SALIDA, MERMA).
     */
    public List<AlertaDesabastoDTO> calcularRiesgoDesabasto(Integer centroId, Integer campaniaId) {
        List<AlertaDesabastoDTO> alertas = new ArrayList<>();
        if (campaniaId == null) {
            return alertas;
        }

        List<Movimiento> movimientos = (centroId != null)
                ? movimientoRepository.findByCentroIdAndCampaniaId(centroId, campaniaId)
                : movimientoRepository.findByCampaniaId(campaniaId);

        List<Movimiento> egresosRecientes = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.ENTREGA ||
                             m.getTipo() == TipoMovimiento.TRANSFERENCIA_SALIDA ||
                             m.getTipo() == TipoMovimiento.MERMA)
                .toList();

        List<Articulo> articulos = articuloRepository.findAll();

        for (Articulo art : articulos) {
            BigDecimal stockBigDecimal = (centroId != null)
                    ? obtenerStock(centroId, campaniaId, art.getId())
                    : movimientos.stream()
                        .filter(m -> m.getArticulo() != null && m.getArticulo().getId().equals(art.getId()))
                        .map(m -> {
                            TipoMovimiento t = m.getTipo();
                            if (t == TipoMovimiento.RECEPCION || t == TipoMovimiento.TRANSFERENCIA_ENTRADA || t == TipoMovimiento.AJUSTE_POSITIVO) {
                                return m.getCantidad();
                            } else {
                                return m.getCantidad().negate();
                            }
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

            double stock = stockBigDecimal != null ? Math.max(0, stockBigDecimal.doubleValue()) : 0.0;

            double totalSalidas = egresosRecientes.stream()
                    .filter(m -> m.getArticulo() != null && m.getArticulo().getId().equals(art.getId()))
                    .mapToDouble(m -> m.getCantidad() != null ? m.getCantidad().doubleValue() : 0.0)
                    .sum();

            // Ventana estimada: 3 días para tasa de consumo diario
            double tasaDiaria = totalSalidas / 3.0;

            if (tasaDiaria > 0) {
                int dias = (int) Math.floor(stock / tasaDiaria);
                String nivel = dias <= 2 ? "CRITICO" : (dias <= 5 ? "MODERADO" : "ESTABLE");
                alertas.add(new AlertaDesabastoDTO(art.getNombre(), stock, Math.round(tasaDiaria * 100.0) / 100.0, dias, nivel));
            } else if (stock <= 5.0 && stock > 0.0) {
                // Si el stock es mínimo y no ha salido recientemente, alerta preventiva
                alertas.add(new AlertaDesabastoDTO(art.getNombre(), stock, 0.0, 1, "MODERADO"));
            }
        }

        // Ordenar primero las alertas críticas, luego moderadas, luego estables
        alertas.sort((a, b) -> {
            int ordenA = "CRITICO".equals(a.getNivelRiesgo()) ? 0 : ("MODERADO".equals(a.getNivelRiesgo()) ? 1 : 2);
            int ordenB = "CRITICO".equals(b.getNivelRiesgo()) ? 0 : ("MODERADO".equals(b.getNivelRiesgo()) ? 1 : 2);
            if (ordenA != ordenB) return Integer.compare(ordenA, ordenB);
            return Integer.compare(a.getDiasRestantes() != null ? a.getDiasRestantes() : 999,
                                   b.getDiasRestantes() != null ? b.getDiasRestantes() : 999);
        });

        return alertas;
    }
}

