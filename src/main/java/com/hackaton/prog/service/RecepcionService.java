package com.hackaton.prog.service;

import com.hackaton.prog.dto.ArticuloItemDTO;
import com.hackaton.prog.dto.RecepcionRequestDTO;
import com.hackaton.prog.dto.RecepcionResponseDTO;
import com.hackaton.prog.dto.ResumenRecepcionDTO;
import com.hackaton.prog.model.Articulo;
import com.hackaton.prog.model.Campania;
import com.hackaton.prog.model.Centro;
import com.hackaton.prog.model.CentroCampania;
import com.hackaton.prog.model.Movimiento;
import com.hackaton.prog.model.enums.CategoriaArticulo;
import com.hackaton.prog.model.enums.UnidadMedida;
import com.hackaton.prog.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecepcionService {

    private final MovimientoService movimientoService;
    private final ArticuloRepository articuloRepository;
    private final CentroRepository centroRepository;
    private final CampaniaRepository campaniaRepository;
    private final CentroCampaniaRepository centroCampaniaRepository;
    private final MovimientoRepository movimientoRepository;

    public RecepcionService(MovimientoService movimientoService,
                            ArticuloRepository articuloRepository,
                            CentroRepository centroRepository,
                            CampaniaRepository campaniaRepository,
                            CentroCampaniaRepository centroCampaniaRepository,
                            MovimientoRepository movimientoRepository) {
        this.movimientoService = movimientoService;
        this.articuloRepository = articuloRepository;
        this.centroRepository = centroRepository;
        this.campaniaRepository = campaniaRepository;
        this.centroCampaniaRepository = centroCampaniaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Registra una recepción de donación con persistencia directa en TiDB Cloud vía JPA.
     */
    @Transactional
    public RecepcionResponseDTO registrarRecepcion(RecepcionRequestDTO request) {
        if (request.getCantidad() == null || request.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad recibida debe ser un número positivo mayor a cero.");
        }

        // Resolver o registrar el artículo
        Integer articuloId = request.getArticuloId();
        if (articuloId == null || articuloId <= 0) {
            if (request.getArticuloNombre() != null && !request.getArticuloNombre().trim().isEmpty()) {
                articuloId = resolverOCrearArticulo(
                        request.getArticuloNombre().trim(),
                        request.getCategoria(),
                        request.getUnidad()
                );
            }
        }

        if (articuloId == null || articuloId <= 0) {
            throw new IllegalArgumentException("Debes seleccionar o describir un artículo válido para registrar.");
        }

        Integer centroId = request.getCentroId() != null ? request.getCentroId() : 1;
        Integer campaniaId = request.getCampaniaId() != null ? request.getCampaniaId() : 1;
        Integer usuarioId = request.getUsuarioId() != null ? request.getUsuarioId() : 3;

        boolean esAnonimo = Boolean.TRUE.equals(request.getEsAnonimo()) ||
                (request.getDonanteNombre() == null || request.getDonanteNombre().trim().isEmpty());

        String donanteNombre = esAnonimo ? null : request.getDonanteNombre().trim();

        // 1. Registrar recepción usando MovimientoService (JPA puro en TiDB Cloud)
        Movimiento mov = movimientoService.registrarRecepcion(
                centroId,
                campaniaId,
                articuloId,
                request.getCantidad(),
                usuarioId,
                donanteNombre
        );

        // Forzar flush para que la consulta de stock total incluya inmediatamente el nuevo movimiento
        movimientoRepository.flush();

        // 2. Calcular stock total actualizado del centro en tiempo real
        BigDecimal stockActual = movimientoRepository.calcularStockTotalCentro(centroId);
        if (stockActual == null) {
            stockActual = BigDecimal.ZERO;
        }

        RecepcionResponseDTO response = new RecepcionResponseDTO();
        response.setExito(true);
        response.setMovimientoId(mov.getId());
        response.setTipo("RECEPCION");
        response.setCantidadRecibida(mov.getCantidad());
        response.setStockActual(stockActual);
        response.setMensaje("Donación registrada con éxito en el sistema");
        return response;
    }

    /**
     * Resuelve el ID del artículo por nombre o lo registra si no existe.
     */
    private Integer resolverOCrearArticulo(String nombre, String categoria, String unidad) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        String nombreLimpio = nombre.trim();
        List<Articulo> existentes = articuloRepository.findByNombreIgnoreCase(nombreLimpio);
        if (!existentes.isEmpty()) {
            return existentes.get(0).getId();
        }

        // Si no existe, crearlo con enums validados
        CategoriaArticulo cat = CategoriaArticulo.OTRO;
        if (categoria != null && !categoria.trim().isEmpty()) {
            try {
                cat = CategoriaArticulo.desdeValorDb(categoria);
            } catch (Exception e) {
                cat = CategoriaArticulo.OTRO;
            }
        }

        UnidadMedida uni = UnidadMedida.PIEZA;
        if (unidad != null && !unidad.trim().isEmpty()) {
            try {
                uni = UnidadMedida.desdeValorDb(unidad);
            } catch (Exception e) {
                uni = UnidadMedida.PIEZA;
            }
        }

        Articulo nuevo = new Articulo(nombreLimpio, cat, uni);
        Articulo guardado = articuloRepository.save(nuevo);
        return guardado.getId();
    }

    /**
     * Lista los artículos para el catálogo/selector utilizando ArticuloRepository.
     */
    @Transactional(readOnly = true)
    public List<ArticuloItemDTO> listarArticulos(String categoria) {
        List<Articulo> articulos;
        if (categoria != null && !categoria.trim().isEmpty() && !categoria.equalsIgnoreCase("TODOS")) {
            try {
                CategoriaArticulo cat = CategoriaArticulo.desdeValorDb(categoria);
                articulos = articuloRepository.findByCategoria(cat);
            } catch (Exception e) {
                articulos = articuloRepository.findAll();
            }
        } else {
            articulos = articuloRepository.findAll();
        }

        List<ArticuloItemDTO> dtos = new ArrayList<>();
        for (Articulo art : articulos) {
            dtos.add(new ArticuloItemDTO(
                    art.getId(),
                    art.getNombre(),
                    art.getCategoria() != null ? art.getCategoria().getValorDb() : "otro",
                    art.getUnidad() != null ? art.getUnidad().getValorDb() : "pieza"
            ));
        }
        return dtos;
    }

    /**
     * Consulta el resumen de la campaña activa y el stock acumulado para el centro
     * mediante repositorios JPA en tiempo real (sin depender de Stored Procedures ni JdbcTemplate).
     */
    @Transactional(readOnly = true)
    public ResumenRecepcionDTO obtenerResumenCentro(Integer centroId) {
        return obtenerResumenCentro(centroId, null);
    }

    @Transactional(readOnly = true)
    public ResumenRecepcionDTO obtenerResumenCentro(Integer centroId, Integer campaniaIdParam) {
        List<Centro> todosCentros = centroRepository.findByActivoTrue();
        if (todosCentros == null) {
            todosCentros = new ArrayList<>();
        }

        if ((centroId == null || centroId <= 0) && !todosCentros.isEmpty()) {
            centroId = todosCentros.get(0).getId();
        } else if (centroId == null || centroId <= 0) {
            centroId = 1;
        }

        final Integer finalCentroId = centroId;
        Centro centro = centroRepository.findById(finalCentroId).orElse(null);
        String centroNombre = (centro != null && centro.getNombre() != null)
                ? centro.getNombre() : "Campus Central - Explanada";

        // Obtener campañas activas relacionadas con este centro
        List<CentroCampania> asignadas = centroCampaniaRepository.findByCentroIdAndActivoTrue(finalCentroId);
        List<com.hackaton.prog.dto.OpcionSimpleDTO> campaniasOpciones = new ArrayList<>();
        Campania campania = null;

        if (!asignadas.isEmpty()) {
            for (CentroCampania cc : asignadas) {
                if (cc.getCampania() != null && Boolean.TRUE.equals(cc.getCampania().getActivo())) {
                    campaniasOpciones.add(new com.hackaton.prog.dto.OpcionSimpleDTO(
                            cc.getCampania().getId(),
                            cc.getCampania().getNombre()
                    ));
                    if (campaniaIdParam != null && cc.getCampania().getId().equals(campaniaIdParam)) {
                        campania = cc.getCampania();
                    } else if (campania == null && campaniaIdParam == null) {
                        campania = cc.getCampania();
                    }
                }
            }
        }

        // Si el centro no tiene campañas asignadas específicamente, cargar campañas activas globales
        if (campaniasOpciones.isEmpty()) {
            List<Campania> activasGlobal = campaniaRepository.findByActivoTrue();
            for (Campania c : activasGlobal) {
                campaniasOpciones.add(new com.hackaton.prog.dto.OpcionSimpleDTO(c.getId(), c.getNombre()));
                if (campaniaIdParam != null && c.getId().equals(campaniaIdParam)) {
                    campania = c;
                } else if (campania == null && campaniaIdParam == null) {
                    campania = c;
                }
            }
        }

        if (campania == null && campaniaIdParam != null) {
            campania = campaniaRepository.findById(campaniaIdParam).orElse(null);
        }

        Integer campaniaId = (campania != null) ? campania.getId() : 1;
        String campaniaNombre = (campania != null && campania.getNombre() != null)
                ? campania.getNombre() : "Plan de Contingencia Huracán 2026";
        BigDecimal metaTotal = (campania != null && campania.getMetaUnidades() != null)
                ? campania.getMetaUnidades() : new BigDecimal("5000.00");

        // Stock actual acumulado en este centro
        BigDecimal stockActual = movimientoRepository.calcularStockTotalCentro(finalCentroId);
        if (stockActual == null) {
            stockActual = BigDecimal.ZERO;
        }

        ResumenRecepcionDTO resumen = new ResumenRecepcionDTO();
        resumen.setCentroId(finalCentroId);
        resumen.setCentroNombre(centroNombre);
        resumen.setCampaniaId(campaniaId);
        resumen.setCampaniaNombre(campaniaNombre);
        resumen.setCampaniasActivas(campaniasOpciones);
        resumen.setMetaTotal(metaTotal);
        resumen.setStockActual(stockActual);

        // Lista de centros activos disponibles para que el usuario elija su sede si no la tiene asignada
        List<com.hackaton.prog.dto.OpcionSimpleDTO> centrosDisponibles = todosCentros.stream()
                .map(c -> new com.hackaton.prog.dto.OpcionSimpleDTO(c.getId(), c.getNombre()))
                .collect(Collectors.toList());
        resumen.setCentrosDisponibles(centrosDisponibles);

        // Porcentaje de avance hacia la meta
        BigDecimal porcentajeAvance = BigDecimal.ZERO;
        if (metaTotal.compareTo(BigDecimal.ZERO) > 0) {
            porcentajeAvance = stockActual
                    .multiply(new BigDecimal("100"))
                    .divide(metaTotal, 2, RoundingMode.HALF_UP);
            if (porcentajeAvance.compareTo(new BigDecimal("100")) > 0) {
                porcentajeAvance = new BigDecimal("100.00");
            }
        }
        resumen.setPorcentajeAvance(porcentajeAvance);

        return resumen;
    }
}
