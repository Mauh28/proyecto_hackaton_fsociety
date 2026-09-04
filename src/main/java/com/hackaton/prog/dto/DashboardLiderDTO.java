package com.hackaton.prog.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardLiderDTO {

    private Integer campaniaId;
    private String campaniaNombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal metaUnidades;
    private Boolean activo;

    // Métricas analíticas agregadas
    private BigDecimal stockActual;
    private BigDecimal totalRecibido;
    private BigDecimal totalEntregado;
    private BigDecimal totalMermas;
    private BigDecimal porcentajeAvance;

    // Centros
    private List<CentroAporteCampaniaDTO> centrosParticipantes = new ArrayList<>();
    private List<OpcionSimpleDTO> centrosDisponibles = new ArrayList<>();
    private List<OpcionSimpleDTO> campaniasDisponibles = new ArrayList<>();

    public DashboardLiderDTO() {
    }

    public DashboardLiderDTO(Integer campaniaId, String campaniaNombre, String descripcion,
                              LocalDate fechaInicio, LocalDate fechaFin, BigDecimal metaUnidades,
                              Boolean activo, BigDecimal stockActual, BigDecimal totalRecibido,
                              BigDecimal totalEntregado, BigDecimal totalMermas,
                              BigDecimal porcentajeAvance,
                              List<CentroAporteCampaniaDTO> centrosParticipantes,
                              List<OpcionSimpleDTO> centrosDisponibles) {
        this.campaniaId = campaniaId;
        this.campaniaNombre = campaniaNombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.metaUnidades = metaUnidades != null ? metaUnidades : BigDecimal.ZERO;
        this.activo = activo != null ? activo : true;
        this.stockActual = stockActual != null ? stockActual : BigDecimal.ZERO;
        this.totalRecibido = totalRecibido != null ? totalRecibido : BigDecimal.ZERO;
        this.totalEntregado = totalEntregado != null ? totalEntregado : BigDecimal.ZERO;
        this.totalMermas = totalMermas != null ? totalMermas : BigDecimal.ZERO;
        this.porcentajeAvance = porcentajeAvance != null ? porcentajeAvance : BigDecimal.ZERO;
        this.centrosParticipantes = centrosParticipantes != null ? centrosParticipantes : new ArrayList<>();
        this.centrosDisponibles = centrosDisponibles != null ? centrosDisponibles : new ArrayList<>();
    }

    public Integer getCampaniaId() {
        return campaniaId;
    }

    public void setCampaniaId(Integer campaniaId) {
        this.campaniaId = campaniaId;
    }

    public String getCampaniaNombre() {
        return campaniaNombre;
    }

    public void setCampaniaNombre(String campaniaNombre) {
        this.campaniaNombre = campaniaNombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public BigDecimal getMetaUnidades() {
        return metaUnidades;
    }

    public void setMetaUnidades(BigDecimal metaUnidades) {
        this.metaUnidades = metaUnidades;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public void setStockActual(BigDecimal stockActual) {
        this.stockActual = stockActual;
    }

    public BigDecimal getTotalRecibido() {
        return totalRecibido;
    }

    public void setTotalRecibido(BigDecimal totalRecibido) {
        this.totalRecibido = totalRecibido;
    }

    public BigDecimal getTotalEntregado() {
        return totalEntregado;
    }

    public void setTotalEntregado(BigDecimal totalEntregado) {
        this.totalEntregado = totalEntregado;
    }

    public BigDecimal getTotalMermas() {
        return totalMermas;
    }

    public void setTotalMermas(BigDecimal totalMermas) {
        this.totalMermas = totalMermas;
    }

    public BigDecimal getPorcentajeAvance() {
        return porcentajeAvance;
    }

    public void setPorcentajeAvance(BigDecimal porcentajeAvance) {
        this.porcentajeAvance = porcentajeAvance;
    }

    public List<CentroAporteCampaniaDTO> getCentrosParticipantes() {
        return centrosParticipantes;
    }

    public void setCentrosParticipantes(List<CentroAporteCampaniaDTO> centrosParticipantes) {
        this.centrosParticipantes = centrosParticipantes;
    }

    public List<OpcionSimpleDTO> getCentrosDisponibles() {
        return centrosDisponibles;
    }

    public void setCentrosDisponibles(List<OpcionSimpleDTO> centrosDisponibles) {
        this.centrosDisponibles = centrosDisponibles;
    }

    public List<OpcionSimpleDTO> getCampaniasDisponibles() {
        return campaniasDisponibles;
    }

    public void setCampaniasDisponibles(List<OpcionSimpleDTO> campaniasDisponibles) {
        this.campaniasDisponibles = campaniasDisponibles;
    }
}
