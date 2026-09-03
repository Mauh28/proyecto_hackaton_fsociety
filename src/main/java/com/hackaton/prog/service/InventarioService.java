package com.hackaton.prog.service;

import com.hackaton.prog.exception.StockInsuficienteException;
import com.hackaton.prog.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class InventarioService {

    private final MovimientoRepository movimientoRepository;

    public InventarioService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
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
}
