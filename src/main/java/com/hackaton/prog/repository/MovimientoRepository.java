package com.hackaton.prog.repository;

import com.hackaton.prog.model.Movimiento;
import com.hackaton.prog.model.enums.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findByCentroIdOrderByFechaDesc(Integer centroId);

    List<Movimiento> findByCampaniaIdOrderByFechaDesc(Integer campaniaId);

    List<Movimiento> findByTipo(TipoMovimiento tipo);

    List<Movimiento> findByCentroIdAndCampaniaIdOrderByFechaDesc(Integer centroId, Integer campaniaId);

    /**
     * Calcula el stock disponible en tiempo real para la combinación (Centro, Campaña, Artículo)
     * Suma recepciones, transferencias entrantes y ajustes positivos.
     * Resta entregas, mermas, transferencias salientes y ajustes negativos.
     */
    @Query("SELECT COALESCE(SUM(CASE " +
           "  WHEN m.tipo IN (com.hackaton.prog.model.enums.TipoMovimiento.RECEPCION, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.TRANSFERENCIA_ENTRADA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.AJUSTE_POSITIVO) THEN m.cantidad " +
           "  WHEN m.tipo IN (com.hackaton.prog.model.enums.TipoMovimiento.ENTREGA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.MERMA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.TRANSFERENCIA_SALIDA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.AJUSTE_NEGATIVO) THEN -m.cantidad " +
           "  ELSE 0 END), 0) " +
           "FROM Movimiento m " +
           "WHERE m.centro.id = :centroId AND m.campania.id = :campaniaId AND m.articulo.id = :articuloId")
    BigDecimal calcularStock(@Param("centroId") Integer centroId,
                             @Param("campaniaId") Integer campaniaId,
                             @Param("articuloId") Integer articuloId);

    @Query("SELECT COALESCE(SUM(CASE " +
           "  WHEN m.tipo IN (com.hackaton.prog.model.enums.TipoMovimiento.RECEPCION, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.TRANSFERENCIA_ENTRADA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.AJUSTE_POSITIVO) THEN m.cantidad " +
           "  WHEN m.tipo IN (com.hackaton.prog.model.enums.TipoMovimiento.ENTREGA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.MERMA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.TRANSFERENCIA_SALIDA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.AJUSTE_NEGATIVO) THEN -m.cantidad " +
           "  ELSE 0 END), 0) " +
           "FROM Movimiento m " +
           "WHERE m.centro.id = :centroId")
    BigDecimal calcularStockTotalCentro(@Param("centroId") Integer centroId);

    @Query("SELECT COALESCE(SUM(m.cantidad), 0) FROM Movimiento m " +
           "WHERE m.centro.id = :centroId AND m.tipo = com.hackaton.prog.model.enums.TipoMovimiento.MERMA")
    BigDecimal calcularTotalMermasCentro(@Param("centroId") Integer centroId);

    @Query("SELECT COALESCE(SUM(CASE " +
           "  WHEN m.tipo IN (com.hackaton.prog.model.enums.TipoMovimiento.RECEPCION, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.TRANSFERENCIA_ENTRADA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.AJUSTE_POSITIVO) THEN m.cantidad " +
           "  WHEN m.tipo IN (com.hackaton.prog.model.enums.TipoMovimiento.ENTREGA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.MERMA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.TRANSFERENCIA_SALIDA, " +
           "                  com.hackaton.prog.model.enums.TipoMovimiento.AJUSTE_NEGATIVO) THEN -m.cantidad " +
           "  ELSE 0 END), 0) " +
           "FROM Movimiento m")
    BigDecimal calcularStockGlobal();

    @Query("SELECT COALESCE(SUM(m.cantidad), 0) FROM Movimiento m " +
           "WHERE m.tipo = com.hackaton.prog.model.enums.TipoMovimiento.MERMA")
    BigDecimal calcularMermaGlobal();

    @Query("SELECT m.articulo.nombre FROM Movimiento m " +
           "WHERE m.tipo = com.hackaton.prog.model.enums.TipoMovimiento.RECEPCION " +
           "GROUP BY m.articulo.id, m.articulo.nombre " +
           "ORDER BY SUM(m.cantidad) DESC LIMIT 1")
    String obtenerNombreArticuloMasDonado();

    List<Movimiento> findTop10ByCentroIdOrderByFechaDesc(Integer centroId);
}
