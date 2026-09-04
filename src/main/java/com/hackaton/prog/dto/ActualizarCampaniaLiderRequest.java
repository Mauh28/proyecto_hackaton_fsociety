package com.hackaton.prog.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ActualizarCampaniaLiderRequest {

    private BigDecimal metaUnidades;
    private String descripcion;
    private LocalDate fechaFin;

    public ActualizarCampaniaLiderRequest() {
    }

    public BigDecimal getMetaUnidades() {
        return metaUnidades;
    }

    public void setMetaUnidades(BigDecimal metaUnidades) {
        this.metaUnidades = metaUnidades;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}
