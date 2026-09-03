package com.hackaton.prog.model;

import com.hackaton.prog.model.enums.EstadoTransferencia;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferencias")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_origen_id", nullable = false)
    private Centro centroOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_destino_id", nullable = false)
    private Centro centroDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campania_id", nullable = false)
    private Campania campania;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(nullable = false)
    private EstadoTransferencia estado = EstadoTransferencia.PENDIENTE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoTransferencia.PENDIENTE;
        }
    }

    public Transferencia() {
    }

    public Transferencia(Centro centroOrigen, Centro centroDestino, Campania campania, Articulo articulo, BigDecimal cantidad, EstadoTransferencia estado) {
        this.centroOrigen = centroOrigen;
        this.centroDestino = centroDestino;
        this.campania = campania;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.estado = estado != null ? estado : EstadoTransferencia.PENDIENTE;
        this.fecha = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Centro getCentroOrigen() {
        return centroOrigen;
    }

    public void setCentroOrigen(Centro centroOrigen) {
        this.centroOrigen = centroOrigen;
    }

    public Centro getCentroDestino() {
        return centroDestino;
    }

    public void setCentroDestino(Centro centroDestino) {
        this.centroDestino = centroDestino;
    }

    public Campania getCampania() {
        return campania;
    }

    public void setCampania(Campania campania) {
        this.campania = campania;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public EstadoTransferencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoTransferencia estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
