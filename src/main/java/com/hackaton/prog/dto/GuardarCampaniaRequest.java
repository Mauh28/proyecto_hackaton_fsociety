package com.hackaton.prog.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GuardarCampaniaRequest {

    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String descripcion;
    private BigDecimal metaUnidades;
    private Boolean activo;

    public GuardarCampaniaRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMetaUnidades() {
        return metaUnidades;
    }

    public void setMetaUnidades(BigDecimal metaUnidades) {
        this.metaUnidades = metaUnidades;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
