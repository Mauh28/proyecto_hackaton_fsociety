package com.hackaton.prog.model;

import com.hackaton.prog.model.enums.CategoriaArticulo;
import com.hackaton.prog.model.enums.UnidadMedida;
import jakarta.persistence.*;

@Entity
@Table(name = "articulos")
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private CategoriaArticulo categoria;

    @Column(nullable = false)
    private UnidadMedida unidad;

    public Articulo() {
    }

    public Articulo(String nombre, CategoriaArticulo categoria, UnidadMedida unidad) {
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

    public CategoriaArticulo getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaArticulo categoria) {
        this.categoria = categoria;
    }

    public UnidadMedida getUnidad() {
        return unidad;
    }

    public void setUnidad(UnidadMedida unidad) {
        this.unidad = unidad;
    }
}
