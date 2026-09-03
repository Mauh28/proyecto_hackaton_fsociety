package com.hackaton.prog.dto;

import java.math.BigDecimal;

public class RecepcionResponseDTO {

    private boolean exito;
    private Integer movimientoId;
    private String tipo;
    private BigDecimal cantidadRecibida;
    private BigDecimal stockActual;
    private String mensaje;

    public RecepcionResponseDTO() {
    }

    public RecepcionResponseDTO(boolean exito, Integer movimientoId, String tipo, BigDecimal cantidadRecibida,
                                BigDecimal stockActual, String mensaje) {
        this.exito = exito;
        this.movimientoId = movimientoId;
        this.tipo = tipo;
        this.cantidadRecibida = cantidadRecibida;
        this.stockActual = stockActual;
        this.mensaje = mensaje;
    }

    public static RecepcionResponseDTO error(String mensaje) {
        RecepcionResponseDTO dto = new RecepcionResponseDTO();
        dto.setExito(false);
        dto.setMensaje(mensaje);
        return dto;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public Integer getMovimientoId() {
        return movimientoId;
    }

    public void setMovimientoId(Integer movimientoId) {
        this.movimientoId = movimientoId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getCantidadRecibida() {
        return cantidadRecibida;
    }

    public void setCantidadRecibida(BigDecimal cantidadRecibida) {
        this.cantidadRecibida = cantidadRecibida;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public void setStockActual(BigDecimal stockActual) {
        this.stockActual = stockActual;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
