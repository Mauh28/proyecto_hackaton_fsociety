package com.hackaton.prog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoHistorialDTO {

    private Integer id;
    private String tipo;
    private String articuloNombre;
    private String unidad;
    private BigDecimal cantidad;
    private String detalle;
    private String autor;
    private LocalDateTime fecha;

    public MovimientoHistorialDTO() {
    }

    public MovimientoHistorialDTO(Integer id, String tipo, String articuloNombre, String unidad,
                                  BigDecimal cantidad, String detalle, String autor, LocalDateTime fecha) {
        this.id = id;
        this.tipo = tipo;
        this.articuloNombre = articuloNombre;
        this.unidad = unidad;
        this.cantidad = cantidad;
        this.detalle = detalle;
        this.autor = autor;
        this.fecha = fecha;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getArticuloNombre() {
        return articuloNombre;
    }

    public void setArticuloNombre(String articuloNombre) {
        this.articuloNombre = articuloNombre;
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

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
