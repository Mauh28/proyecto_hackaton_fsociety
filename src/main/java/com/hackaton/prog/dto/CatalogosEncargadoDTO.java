package com.hackaton.prog.dto;

import java.util.ArrayList;
import java.util.List;

public class CatalogosEncargadoDTO {

    private Integer centroId;
    private Integer campaniaId;
    private String campaniaNombre;
    private List<ArticuloStockDTO> articulos = new ArrayList<>();
    private List<OpcionSimpleDTO> instituciones = new ArrayList<>();
    private List<OpcionSimpleDTO> centrosDestino = new ArrayList<>();

    public CatalogosEncargadoDTO() {
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

    public String getCampaniaNombre() {
        return campaniaNombre;
    }

    public void setCampaniaNombre(String campaniaNombre) {
        this.campaniaNombre = campaniaNombre;
    }

    public List<ArticuloStockDTO> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<ArticuloStockDTO> articulos) {
        this.articulos = articulos;
    }

    public List<OpcionSimpleDTO> getInstituciones() {
        return instituciones;
    }

    public void setInstituciones(List<OpcionSimpleDTO> instituciones) {
        this.instituciones = instituciones;
    }

    public List<OpcionSimpleDTO> getCentrosDestino() {
        return centrosDestino;
    }

    public void setCentrosDestino(List<OpcionSimpleDTO> centrosDestino) {
        this.centrosDestino = centrosDestino;
    }
}
