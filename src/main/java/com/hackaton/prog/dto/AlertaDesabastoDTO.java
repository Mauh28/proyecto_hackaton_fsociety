package com.hackaton.prog.dto;

public class AlertaDesabastoDTO {

    private String articulo;
    private Double stockActual;
    private Double consumoDiarioPromedio;
    private Integer diasRestantes;
    private String nivelRiesgo; // "CRITICO", "MODERADO", "ESTABLE"

    public AlertaDesabastoDTO() {
    }

    public AlertaDesabastoDTO(String articulo, Double stockActual, Double consumoDiarioPromedio, Integer diasRestantes, String nivelRiesgo) {
        this.articulo = articulo;
        this.stockActual = stockActual;
        this.consumoDiarioPromedio = consumoDiarioPromedio;
        this.diasRestantes = diasRestantes;
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public Double getStockActual() {
        return stockActual;
    }

    public void setStockActual(Double stockActual) {
        this.stockActual = stockActual;
    }

    public Double getConsumoDiarioPromedio() {
        return consumoDiarioPromedio;
    }

    public void setConsumoDiarioPromedio(Double consumoDiarioPromedio) {
        this.consumoDiarioPromedio = consumoDiarioPromedio;
    }

    public Integer getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(Integer diasRestantes) {
        this.diasRestantes = diasRestantes;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }
}
