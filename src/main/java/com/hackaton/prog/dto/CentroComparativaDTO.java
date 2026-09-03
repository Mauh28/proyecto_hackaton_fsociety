package com.hackaton.prog.dto;

import java.math.BigDecimal;

public class CentroComparativaDTO {

    private Integer id;
    private String nombre;
    private String encargado;
    private BigDecimal stock;

    public CentroComparativaDTO() {
    }

    public CentroComparativaDTO(Integer id, String nombre, String encargado, BigDecimal stock) {
        this.id = id;
        this.nombre = nombre;
        this.encargado = encargado != null ? encargado : "Sin Asignar";
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

    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }
}
