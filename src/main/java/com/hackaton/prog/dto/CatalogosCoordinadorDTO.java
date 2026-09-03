package com.hackaton.prog.dto;

import java.util.ArrayList;
import java.util.List;

public class CatalogosCoordinadorDTO {

    private List<OpcionSimpleDTO> encargados = new ArrayList<>();
    private List<OpcionSimpleDTO> campanias = new ArrayList<>();

    public CatalogosCoordinadorDTO() {
    }

    public CatalogosCoordinadorDTO(List<OpcionSimpleDTO> encargados, List<OpcionSimpleDTO> campanias) {
        this.encargados = encargados != null ? encargados : new ArrayList<>();
        this.campanias = campanias != null ? campanias : new ArrayList<>();
    }

    public List<OpcionSimpleDTO> getEncargados() {
        return encargados;
    }

    public void setEncargados(List<OpcionSimpleDTO> encargados) {
        this.encargados = encargados;
    }

    public List<OpcionSimpleDTO> getCampanias() {
        return campanias;
    }

    public void setCampanias(List<OpcionSimpleDTO> campanias) {
        this.campanias = campanias;
    }
}
