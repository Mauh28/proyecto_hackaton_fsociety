package com.hackaton.prog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "centros")
public class Centro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String institucion;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ubicacion;

    @Column(nullable = false)
    private Boolean activo = true;

    public Centro() {
    }

    public Centro(String nombre, String institucion, String ubicacion, Boolean activo) {
        this.nombre = nombre;
        this.institucion = institucion;
        this.ubicacion = ubicacion;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
