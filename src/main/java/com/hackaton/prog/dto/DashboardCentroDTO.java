package com.hackaton.prog.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardCentroDTO {

    private Integer centroId;
    private String centroNombre;
    private Integer campaniaId;
    private String campaniaNombre;
    private BigDecimal stockTotal;
    private BigDecimal totalMermasMes;
    private BigDecimal metaCampania;
    private List<MovimientoHistorialDTO> historial = new ArrayList<>();
    private List<AlertaDesabastoDTO> alertasDesabasto = new ArrayList<>();

    public DashboardCentroDTO() {
    }

    public DashboardCentroDTO(Integer centroId, String centroNombre, Integer campaniaId,
                              String campaniaNombre, BigDecimal stockTotal, BigDecimal totalMermasMes,
                              BigDecimal metaCampania, List<MovimientoHistorialDTO> historial,
                              List<AlertaDesabastoDTO> alertasDesabasto) {
        this.centroId = centroId;
        this.centroNombre = centroNombre;
        this.campaniaId = campaniaId;
        this.campaniaNombre = campaniaNombre;
        this.stockTotal = stockTotal != null ? stockTotal : BigDecimal.ZERO;
        this.totalMermasMes = totalMermasMes != null ? totalMermasMes : BigDecimal.ZERO;
        this.metaCampania = metaCampania != null ? metaCampania : BigDecimal.ZERO;
        this.historial = historial != null ? historial : new ArrayList<>();
        this.alertasDesabasto = alertasDesabasto != null ? alertasDesabasto : new ArrayList<>();
    }

    public Integer getCentroId() {
        return centroId;
    }

    public void setCentroId(Integer centroId) {
        this.centroId = centroId;
    }

    public String getCentroNombre() {
        return centroNombre;
    }

    public void setCentroNombre(String centroNombre) {
        this.centroNombre = centroNombre;
    }

    public Integer getCampaniaId() {
        return campaniaId;
    }

    public void setCampaniaId(Integer campaniaId) {
        this.campaniaId = campaniaId;
    }

    public String getCampaniaNombre() {
        return campaniaNombre;
    }

    public void setCampaniaNombre(String campaniaNombre) {
        this.campaniaNombre = campaniaNombre;
    }

    public BigDecimal getStockTotal() {
        return stockTotal;
    }

    public void setStockTotal(BigDecimal stockTotal) {
        this.stockTotal = stockTotal;
    }

    public BigDecimal getTotalMermasMes() {
        return totalMermasMes;
    }

    public void setTotalMermasMes(BigDecimal totalMermasMes) {
        this.totalMermasMes = totalMermasMes;
    }

    public BigDecimal getMetaCampania() {
        return metaCampania;
    }

    public void setMetaCampania(BigDecimal metaCampania) {
        this.metaCampania = metaCampania;
    }

    private List<OpcionSimpleDTO> campaniasDisponibles = new ArrayList<>();
    private List<OpcionSimpleDTO> centrosDisponibles = new ArrayList<>();

    public List<MovimientoHistorialDTO> getHistorial() {
        return historial;
    }

    public void setHistorial(List<MovimientoHistorialDTO> historial) {
        this.historial = historial;
    }

    public List<OpcionSimpleDTO> getCampaniasDisponibles() {
        return campaniasDisponibles;
    }

    public void setCampaniasDisponibles(List<OpcionSimpleDTO> campaniasDisponibles) {
        this.campaniasDisponibles = campaniasDisponibles;
    }

    public List<OpcionSimpleDTO> getCentrosDisponibles() {
        return centrosDisponibles;
    }

    public void setCentrosDisponibles(List<OpcionSimpleDTO> centrosDisponibles) {
        this.centrosDisponibles = centrosDisponibles;
    }

    public List<AlertaDesabastoDTO> getAlertasDesabasto() {
        return alertasDesabasto;
    }

    public void setAlertasDesabasto(List<AlertaDesabastoDTO> alertasDesabasto) {
        this.alertasDesabasto = alertasDesabasto;
    }
}
