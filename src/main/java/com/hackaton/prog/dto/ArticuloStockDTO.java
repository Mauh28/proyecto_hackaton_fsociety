package com.hackaton.prog.dto;

import java.math.BigDecimal;

public class ArticuloStockDTO {

    private Integer id;
    private String nombre;
    private String categoria;
    private String unidad;
    private BigDecimal stock;

    public ArticuloStockDTO() {
    }

    public ArticuloStockDTO(Integer id, String nombre, String categoria, String unidad, BigDecimal stock) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.unidad = unidad;
        this.stock = stock != null ? stock : BigDecimal.ZERO;
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

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }
}
