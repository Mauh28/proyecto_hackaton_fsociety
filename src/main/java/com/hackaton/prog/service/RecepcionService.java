package com.hackaton.prog.service;

import com.hackaton.prog.dto.ArticuloItemDTO;
import com.hackaton.prog.dto.RecepcionRequestDTO;
import com.hackaton.prog.dto.RecepcionResponseDTO;
import com.hackaton.prog.dto.ResumenRecepcionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
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

    public RecepcionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Registra una recepción de donación invocando el procedimiento almacenado oficial:
     * sp_registrar_recepcion_donacion(p_centro_id, p_campania_id, p_articulo_id, p_cantidad, p_usuario_id, p_es_anonimo, p_donante_nombre, p_donante_contacto)
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
        String donanteContacto = esAnonimo ? null : (request.getDonanteContacto() != null ? request.getDonanteContacto().trim() : null);

        String sql = "CALL sp_registrar_recepcion_donacion(?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(
                    sql,
                    centroId,
                    campaniaId,
                    articuloId,
                    request.getCantidad(),
                    usuarioId,
                    esAnonimo,
                    donanteNombre,
                    donanteContacto
            );

            if (!resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                RecepcionResponseDTO response = new RecepcionResponseDTO();
                response.setExito(true);

                if (row.get("movimiento_id") != null) {
                    response.setMovimientoId(((Number) row.get("movimiento_id")).intValue());
                }
                response.setTipo((String) row.get("tipo"));

                if (row.get("cantidad_recibida") != null) {
                    response.setCantidadRecibida(new BigDecimal(row.get("cantidad_recibida").toString()));
                }
                if (row.get("stock_actual") != null) {
                    response.setStockActual(new BigDecimal(row.get("stock_actual").toString()));
                }
                response.setMensaje((String) row.get("mensaje"));
                return response;
            } else {
                throw new IllegalStateException("No se obtuvo confirmación del procedimiento almacenado.");
            }
        } catch (DataAccessException ex) {
            Throwable root = ex.getRootCause() != null ? ex.getRootCause() : ex;
            String cleanMessage = root.getMessage();
            log.warn("Error devuelto por sp_registrar_recepcion_donacion: {}", cleanMessage);
            throw new IllegalArgumentException(cleanMessage);
        }
    }

    /**
     * Resuelve el ID del artículo por nombre o lo da de alta con sp_crear_articulo.
     */
    private Integer resolverOCrearArticulo(String nombre, String categoria, String unidad) {
        try {
            List<Map<String, Object>> existentes = jdbcTemplate.queryForList(
                    "SELECT id FROM articulos WHERE LOWER(nombre) = LOWER(?) LIMIT 1", nombre
            );
            if (!existentes.isEmpty() && existentes.get(0).get("id") != null) {
                return ((Number) existentes.get(0).get("id")).intValue();
            }

            // Normalizar categoría
            String catNormalizada = "OTRO";
            if (categoria != null && !categoria.trim().isEmpty()) {
                String c = categoria.trim().toUpperCase().replace("-", "_");
                if (c.contains("NO_PERECEDERO")) catNormalizada = "NO_PERECEDERO";
                else if (c.contains("PERECEDERO")) catNormalizada = "PERECEDERO";
                else if (c.contains("LIMPIEZA")) catNormalizada = "LIMPIEZA";
                else if (c.contains("ROPA")) catNormalizada = "ROPA";
                else if (c.contains("MEDICAMENTO")) catNormalizada = "MEDICAMENTO";
            }

            // Normalizar unidad
            String unidadNormalizada = "PIEZA";
            if (unidad != null && !unidad.trim().isEmpty()) {
                String u = unidad.trim().toUpperCase();
                if (u.contains("KG")) unidadNormalizada = "KG";
                else if (u.contains("L")) unidadNormalizada = "L";
                else if (u.contains("BOLSA")) unidadNormalizada = "BOLSA";
                else if (u.contains("CAJA")) unidadNormalizada = "CAJA";
                else if (u.contains("PIEZA") || u.contains("PZA")) unidadNormalizada = "PIEZA";
            }

            List<Map<String, Object>> nuevo = jdbcTemplate.queryForList(
                    "CALL sp_crear_articulo(?, ?, ?)", nombre, catNormalizada, unidadNormalizada
            );
            if (!nuevo.isEmpty() && nuevo.get(0).get("articulo_id") != null) {
                return ((Number) nuevo.get(0).get("articulo_id")).intValue();
            }
        } catch (Exception ex) {
            log.warn("Error al resolver o crear artículo: {}", ex.getMessage());
        }
        return null;
    }

    /**
     * Lista los artículos para el catálogo/selector invocando:
     * sp_listar_articulos(p_categoria)
     */
    public List<ArticuloItemDTO> listarArticulos(String categoria) {
        String sql = "CALL sp_listar_articulos(?)";
        String catParam = null;

        if (categoria != null && !categoria.trim().isEmpty() && !categoria.equalsIgnoreCase("TODOS")) {
            catParam = categoria.trim().toUpperCase().replace("-", "_");
        }

        List<ArticuloItemDTO> articulos = new ArrayList<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, catParam);
            for (Map<String, Object> r : rows) {
                articulos.add(new ArticuloItemDTO(
                        ((Number) r.get("id")).intValue(),
                        (String) r.get("nombre"),
                        (String) r.get("categoria"),
                        (String) r.get("unidad")
                ));
            }
        } catch (Exception ex) {
            log.error("Error al consultar sp_listar_articulos: {}", ex.getMessage(), ex);
        }
        return articulos;
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

        // 1. Consultar campaña activa del centro mediante SP 19
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
            log.warn("No se pudo obtener campaña activa vía SP 19: {}", ex.getMessage());
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
