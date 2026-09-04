package com.hackaton.prog.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ResumenRecepcionDTO {

    private Integer centroId;
    private String centroNombre;
    private Integer campaniaId;
    private String campaniaNombre;
    private BigDecimal stockActual;
    private BigDecimal metaTotal;
    private BigDecimal porcentajeAvance;
    private List<OpcionSimpleDTO> campaniasActivas = new ArrayList<>();

    public ResumenRecepcionDTO() {
    }

    public ResumenRecepcionDTO(Integer centroId, String centroNombre, Integer campaniaId, String campaniaNombre,
                               BigDecimal stockActual, BigDecimal metaTotal, BigDecimal porcentajeAvance) {
        this.centroId = centroId;
        this.centroNombre = centroNombre;
        this.campaniaId = campaniaId;
        this.campaniaNombre = campaniaNombre;
        this.stockActual = stockActual;
        this.metaTotal = metaTotal;
        this.porcentajeAvance = porcentajeAvance;
        this.campaniasActivas = new ArrayList<>();
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

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public void setStockActual(BigDecimal stockActual) {
        this.stockActual = stockActual;
    }

    public BigDecimal getMetaTotal() {
        return metaTotal;
    }

    public void setMetaTotal(BigDecimal metaTotal) {
        this.metaTotal = metaTotal;
    }

    public BigDecimal getPorcentajeAvance() {
        return porcentajeAvance;
    }

    public void setPorcentajeAvance(BigDecimal porcentajeAvance) {
        this.porcentajeAvance = porcentajeAvance;
    }

    public List<OpcionSimpleDTO> getCampaniasActivas() {
        return campaniasActivas;
    }

    public void setCampaniasActivas(List<OpcionSimpleDTO> campaniasActivas) {
        this.campaniasActivas = campaniasActivas;
    }
}
