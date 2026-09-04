package com.hackaton.prog.dto;

public class AsociarCentroCampaniaRequest {

    private Integer centroId;

    public AsociarCentroCampaniaRequest() {
    }

    public AsociarCentroCampaniaRequest(Integer centroId) {
        this.centroId = centroId;
    }

    public Integer getCentroId() {
        return centroId;
    }

    public void setCentroId(Integer centroId) {
        this.centroId = centroId;
    }
}
