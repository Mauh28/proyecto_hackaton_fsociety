package com.hackaton.prog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "instituciones_receptoras")
public class InstitucionReceptora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String direccion;

    public InstitucionReceptora() {
    }

    public InstitucionReceptora(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
