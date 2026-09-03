package com.hackaton.prog.dto;

public class ArticuloItemDTO {

    private Integer id;
    private String nombre;
    private String categoria;
    private String unidad;

    public ArticuloItemDTO() {
    }

    public ArticuloItemDTO(Integer id, String nombre, String categoria, String unidad) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.unidad = unidad;
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
}
