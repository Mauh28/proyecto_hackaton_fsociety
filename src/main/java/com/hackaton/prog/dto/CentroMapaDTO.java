package com.hackaton.prog.dto;

import java.math.BigDecimal;

public class CentroMapaDTO {

    private Integer id;
    private String nombre;
    private String institucion;
    private String ubicacion;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal stockTotal;
    private String encargado;
    private String nivelSuministro;
    private Boolean activo;

    public CentroMapaDTO() {
    }

    public CentroMapaDTO(Integer id, String nombre, String institucion, String ubicacion,
                          BigDecimal latitud, BigDecimal longitud, BigDecimal stockTotal,
                          String encargado, String nivelSuministro, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.institucion = institucion;
        this.ubicacion = ubicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.stockTotal = stockTotal != null ? stockTotal : BigDecimal.ZERO;
        this.encargado = encargado != null ? encargado : "Sin Asignar";
        this.nivelSuministro = nivelSuministro != null ? nivelSuministro : "NORMAL";
        this.activo = activo != null ? activo : true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    public BigDecimal getStockTotal() {
        return stockTotal;
    }

    public void setStockTotal(BigDecimal stockTotal) {
        this.stockTotal = stockTotal;
    }

    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }

    public String getNivelSuministro() {
        return nivelSuministro;
    }

    public void setNivelSuministro(String nivelSuministro) {
        this.nivelSuministro = nivelSuministro;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
