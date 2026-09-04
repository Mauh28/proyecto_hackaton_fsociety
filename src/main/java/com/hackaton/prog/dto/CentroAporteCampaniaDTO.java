package com.hackaton.prog.dto;

import java.math.BigDecimal;

public class CentroAporteCampaniaDTO {

    private Integer idCentro;
    private String nombreCentro;
    private String institucion;
    private String ubicacion;
    private BigDecimal stockAportado;
    private Boolean activo;

    public CentroAporteCampaniaDTO() {
    }

    public CentroAporteCampaniaDTO(Integer idCentro, String nombreCentro, String institucion,
                                   String ubicacion, BigDecimal stockAportado, Boolean activo) {
        this.idCentro = idCentro;
        this.nombreCentro = nombreCentro;
        this.institucion = institucion;
        this.ubicacion = ubicacion;
        this.stockAportado = stockAportado != null ? stockAportado : BigDecimal.ZERO;
        this.activo = activo != null ? activo : true;
    }

    public Integer getIdCentro() {
        return idCentro;
    }

    public void setIdCentro(Integer idCentro) {
        this.idCentro = idCentro;
    }

    public String getNombreCentro() {
        return nombreCentro;
    }

    public void setNombreCentro(String nombreCentro) {
        this.nombreCentro = nombreCentro;
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

    public BigDecimal getStockAportado() {
        return stockAportado;
    }

    public void setStockAportado(BigDecimal stockAportado) {
        this.stockAportado = stockAportado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
