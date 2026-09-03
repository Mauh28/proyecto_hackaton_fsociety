package com.hackaton.prog.dto;

import java.math.BigDecimal;

public class RecepcionRequestDTO {

    private Integer centroId;
    private Integer campaniaId;
    private Integer articuloId;
    private String articuloNombre;
    private String categoria;
    private String unidad;
    private BigDecimal cantidad;
    private Integer usuarioId;
    private Boolean esAnonimo;
    private String donanteNombre;
    private String donanteContacto;

    public RecepcionRequestDTO() {
    }

    public RecepcionRequestDTO(Integer centroId, Integer campaniaId, Integer articuloId, String articuloNombre,
                               String categoria, String unidad, BigDecimal cantidad, Integer usuarioId,
                               Boolean esAnonimo, String donanteNombre, String donanteContacto) {
        this.centroId = centroId;
        this.campaniaId = campaniaId;
        this.articuloId = articuloId;
        this.articuloNombre = articuloNombre;
        this.categoria = categoria;
        this.unidad = unidad;
        this.cantidad = cantidad;
        this.usuarioId = usuarioId;
        this.esAnonimo = esAnonimo;
        this.donanteNombre = donanteNombre;
        this.donanteContacto = donanteContacto;
    }

    public Integer getCentroId() {
        return centroId;
    }

    public void setCentroId(Integer centroId) {
        this.centroId = centroId;
    }

    public Integer getCampaniaId() {
        return campaniaId;
    }

    public void setCampaniaId(Integer campaniaId) {
        this.campaniaId = campaniaId;
    }

    public Integer getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(Integer articuloId) {
        this.articuloId = articuloId;
    }

    public String getArticuloNombre() {
        return articuloNombre;
    }

    public void setArticuloNombre(String articuloNombre) {
        this.articuloNombre = articuloNombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Boolean getEsAnonimo() {
        return esAnonimo;
    }

    public void setEsAnonimo(Boolean esAnonimo) {
        this.esAnonimo = esAnonimo;
    }

    public String getDonanteNombre() {
        return donanteNombre;
    }

    public void setDonanteNombre(String donanteNombre) {
        this.donanteNombre = donanteNombre;
    }

    public String getDonanteContacto() {
        return donanteContacto;
    }

    public void setDonanteContacto(String donanteContacto) {
        this.donanteContacto = donanteContacto;
    }
}
