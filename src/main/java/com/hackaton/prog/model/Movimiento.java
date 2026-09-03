package com.hackaton.prog.model;

import com.hackaton.prog.model.enums.MotivoMovimiento;
import com.hackaton.prog.model.enums.TipoMovimiento;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private TipoMovimiento tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_id", nullable = false)
    private Centro centro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campania_id", nullable = false)
    private Campania campania;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column
    private MotivoMovimiento motivo;

    @Column(name = "motivo_detalle", columnDefinition = "TEXT")
    private String motivoDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donante_id")
    private Donante donante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institucion_receptora_id")
    private InstitucionReceptora institucionReceptora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferencia_id")
    private Transferencia transferencia;

    @Column(name = "entrega_confirmada", nullable = false)
    private Boolean entregaConfirmada = false;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
        if (this.entregaConfirmada == null) {
            this.entregaConfirmada = false;
        }
    }

    public Movimiento() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public Centro getCentro() {
        return centro;
    }

    public void setCentro(Centro centro) {
        this.centro = centro;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public MotivoMovimiento getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoMovimiento motivo) {
        this.motivo = motivo;
    }

    public String getMotivoDetalle() {
        return motivoDetalle;
    }

    public void setMotivoDetalle(String motivoDetalle) {
        this.motivoDetalle = motivoDetalle;
    }

    public Donante getDonante() {
        return donante;
    }

    public void setDonante(Donante donante) {
        this.donante = donante;
    }

    public InstitucionReceptora getInstitucionReceptora() {
        return institucionReceptora;
    }

    public void setInstitucionReceptora(InstitucionReceptora institucionReceptora) {
        this.institucionReceptora = institucionReceptora;
    }

    public Transferencia getTransferencia() {
        return transferencia;
    }

    public void setTransferencia(Transferencia transferencia) {
        this.transferencia = transferencia;
    }

    public Boolean getEntregaConfirmada() {
        return entregaConfirmada;
    }

    public void setEntregaConfirmada(Boolean entregaConfirmada) {
        this.entregaConfirmada = entregaConfirmada;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }
}
