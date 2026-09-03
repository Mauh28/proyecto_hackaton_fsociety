package com.hackaton.prog.service;

import com.hackaton.prog.dto.ArticuloItemDTO;
import com.hackaton.prog.dto.RecepcionRequestDTO;
import com.hackaton.prog.dto.RecepcionResponseDTO;
import com.hackaton.prog.dto.ResumenRecepcionDTO;
import com.hackaton.prog.model.Articulo;
import com.hackaton.prog.model.Movimiento;
import com.hackaton.prog.model.enums.CategoriaArticulo;
import com.hackaton.prog.model.enums.UnidadMedida;
import com.hackaton.prog.repository.ArticuloRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RecepcionService {

    private static final Logger log = LoggerFactory.getLogger(RecepcionService.class);

    private final JdbcTemplate jdbcTemplate;
    private final MovimientoService movimientoService;
    private final ArticuloRepository articuloRepository;

    public RecepcionService(JdbcTemplate jdbcTemplate,
                            MovimientoService movimientoService,
                            ArticuloRepository articuloRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.movimientoService = movimientoService;
        this.articuloRepository = articuloRepository;
    }

    /**
     * Registra una recepción de donación con persistencia directa en TiDB Cloud.
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

        // 2. Calcular stock acumulado del centro y campaña en vivo
        BigDecimal stockActual = BigDecimal.ZERO;
        try {
            BigDecimal stock = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(stock_disponible), 0) FROM v_stock_actual WHERE centro_id = ? AND campania_id = ?",
                    BigDecimal.class,
                    centroId,
                    campaniaId
            );
            if (stock != null) {
                stockActual = stock;
            }
        } catch (Exception ex) {
            log.warn("No se pudo consultar v_stock_actual tras recepcion: {}", ex.getMessage());
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
     * Lista los artículos para el catálogo/selector utilizando ArticuloRepository
     */
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
     * Consulta el resumen de la campaña activa y el stock acumulado para el centro:
     * Utiliza sp_listar_campanias_activas_centro y v_stock_actual.
     */
    public ResumenRecepcionDTO obtenerResumenCentro(Integer centroId) {
        if (centroId == null) {
            centroId = 1; // Centro predeterminado Campus Central
        }

        ResumenRecepcionDTO resumen = new ResumenRecepcionDTO();
        resumen.setCentroId(centroId);
        resumen.setCentroNombre("Campus Central - Explanada");
        resumen.setCampaniaId(1);
        resumen.setCampaniaNombre("Plan de Contingencia Huracán 2026");
        resumen.setMetaTotal(new BigDecimal("5000.00"));
        resumen.setStockActual(BigDecimal.ZERO);
        resumen.setPorcentajeAvance(BigDecimal.ZERO);

        // 1. Consultar campaña activa del centro mediante SP o fallback
        try {
            List<Map<String, Object>> campanias = jdbcTemplate.queryForList(
                    "CALL sp_listar_campanias_activas_centro(?)", centroId
            );
            if (!campanias.isEmpty()) {
                Map<String, Object> c = campanias.get(0);
                if (c.get("campania_id") != null) {
                    resumen.setCampaniaId(((Number) c.get("campania_id")).intValue());
                }
                if (c.get("nombre") != null) {
                    resumen.setCampaniaNombre((String) c.get("nombre"));
                }
                if (c.get("meta_unidades") != null) {
                    resumen.setMetaTotal(new BigDecimal(c.get("meta_unidades").toString()));
                }
            }
        } catch (Exception ex) {
            log.warn("sp_listar_campanias_activas_centro no disponible, usando fallback SQL: {}", ex.getMessage());
            try {
                String sqlCampania = "SELECT c.id AS campania_id, c.nombre, c.meta_unidades " +
                        "FROM campanias c " +
                        "INNER JOIN centros_campanias cc ON c.id = cc.id_campania " +
                        "WHERE cc.id_centro = ? AND cc.activo = TRUE AND c.activo = TRUE LIMIT 1";
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlCampania, centroId);
                if (!rows.isEmpty()) {
                    Map<String, Object> c = rows.get(0);
                    if (c.get("campania_id") != null) resumen.setCampaniaId(((Number) c.get("campania_id")).intValue());
                    if (c.get("nombre") != null) resumen.setCampaniaNombre((String) c.get("nombre"));
                    if (c.get("meta_unidades") != null) resumen.setMetaTotal(new BigDecimal(c.get("meta_unidades").toString()));
                }
            } catch (Exception e2) {
                log.error("Error en fallback campania: {}", e2.getMessage());
            }
        }

        // 2. Consultar nombre del centro
        try {
            List<Map<String, Object>> centros = jdbcTemplate.queryForList(
                    "SELECT nombre FROM centros WHERE id = ?", centroId
            );
            if (!centros.isEmpty() && centros.get(0).get("nombre") != null) {
                resumen.setCentroNombre((String) centros.get(0).get("nombre"));
            }
        } catch (Exception ignored) {
        }

        // 3. Consultar stock actual acumulado en este centro y campaña
        try {
            BigDecimal stock = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(stock_disponible), 0) FROM v_stock_actual WHERE centro_id = ? AND campania_id = ?",
                    BigDecimal.class,
                    centroId,
                    resumen.getCampaniaId()
            );
            if (stock != null) {
                resumen.setStockActual(stock);
            }
        } catch (Exception ex) {
            log.warn("No se pudo calcular stock desde v_stock_actual: {}", ex.getMessage());
        }

        // 4. Calcular porcentaje de avance
        if (resumen.getMetaTotal() != null && resumen.getMetaTotal().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal porcentaje = resumen.getStockActual()
                    .multiply(new BigDecimal("100"))
                    .divide(resumen.getMetaTotal(), 2, RoundingMode.HALF_UP);
            if (porcentaje.compareTo(new BigDecimal("100")) > 0) {
                porcentaje = new BigDecimal("100.00");
            }
            resumen.setPorcentajeAvance(porcentaje);
        }

        return resumen;
    }
}
