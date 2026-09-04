package com.hackaton.prog.dto;

import java.math.BigDecimal;

public class RegistroMovimientoRequest {

    private String tipo; // ENTREGA, TRANSFERENCIA, MERMA, AJUSTE
    private Integer centroId;
    private Integer campaniaId;
    private Integer articuloId;
    private BigDecimal cantidad;
    private Integer usuarioId;

    // Específico para ENTREGA
    private Integer institucionId;
    private String beneficiarioNombre;
    private String tipoEntrega; // "institucion" o "beneficiario"

    // Específico para TRANSFERENCIA
    private Integer centroDestinoId;

    // Específico para MERMA o AJUSTE
    private String motivo; // CADUCIDAD, DANO, PERDIDA, CORRECCION_CONTEO, ERROR_CAPTURA, OTRO
    private String motivoDetalle;

    // Específico para AJUSTE
    private Boolean esPositivo; // true: suma, false: resta

    public RegistroMovimientoRequest() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCentroId() {
        return centroId;
    }

    public void setCentroId(Integer centroId) {
        this.centroId = centroId;
    }

    public Integer getCampaniaId() {
        return campaniaId;
    }

    public void setCampaniaId(Integer campaniaId) {
        this.campaniaId = campaniaId;
    }

    public Integer getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(Integer articuloId) {
        this.articuloId = articuloId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getInstitucionId() {
        return institucionId;
    }

    public void setInstitucionId(Integer institucionId) {
        this.institucionId = institucionId;
    }

    public String getBeneficiarioNombre() {
        return beneficiarioNombre;
    }

    public void setBeneficiarioNombre(String beneficiarioNombre) {
        this.beneficiarioNombre = beneficiarioNombre;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public Integer getCentroDestinoId() {
        return centroDestinoId;
    }

    public void setCentroDestinoId(Integer centroDestinoId) {
        this.centroDestinoId = centroDestinoId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivoDetalle() {
        return motivoDetalle;
    }

    public void setMotivoDetalle(String motivoDetalle) {
        this.motivoDetalle = motivoDetalle;
    }

    public Boolean getEsPositivo() {
        return esPositivo;
    }

    public void setEsPositivo(Boolean esPositivo) {
        this.esPositivo = esPositivo;
    }
}
