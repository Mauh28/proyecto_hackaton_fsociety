package com.hackaton.prog.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "campanias")
public class Campania {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "meta_unidades", precision = 10, scale = 2)
    private java.math.BigDecimal metaUnidades = java.math.BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lider_id")
    private Usuario lider;

    public Campania() {
    }

    public Campania(String nombre, LocalDate fechaInicio, LocalDate fechaFin, String descripcion, Boolean activo, Usuario lider) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.descripcion = descripcion;
        this.activo = activo != null ? activo : true;
        this.lider = lider;
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

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public java.math.BigDecimal getMetaUnidades() {
        return metaUnidades;
    }

    public void setMetaUnidades(java.math.BigDecimal metaUnidades) {
        this.metaUnidades = metaUnidades;
    }

    public Usuario getLider() {
        return lider;
    }

    public void setLider(Usuario lider) {
        this.lider = lider;
    }
}
