package com.hackaton.prog.dto;

public class OpcionSimpleDTO {

    private Integer id;
    private String nombre;

    public OpcionSimpleDTO() {
    }

    public OpcionSimpleDTO(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
}
