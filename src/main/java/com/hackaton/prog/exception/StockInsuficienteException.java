package com.hackaton.prog.exception;

import java.math.BigDecimal;

public class StockInsuficienteException extends RuntimeException {

    private final BigDecimal stockDisponible;
    private final BigDecimal cantidadRequerida;

    public StockInsuficienteException(String mensaje, BigDecimal stockDisponible, BigDecimal cantidadRequerida) {
        super(mensaje);
        this.stockDisponible = stockDisponible;
        this.cantidadRequerida = cantidadRequerida;
    }

    public BigDecimal getStockDisponible() {
        return stockDisponible;
    }

    public BigDecimal getCantidadRequerida() {
        return cantidadRequerida;
    }
}
