package com.hackaton.prog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "centros_campanias")
public class CentroCampania {

    @EmbeddedId
    private CentroCampaniaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idCentro")
    @JoinColumn(name = "id_centro")
    private Centro centro;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idCampania")
    @JoinColumn(name = "id_campania")
    private Campania campania;

    @Column(nullable = false)
    private Boolean activo = true;

    public CentroCampania() {
    }

    public CentroCampania(Centro centro, Campania campania, Boolean activo) {
        this.centro = centro;
        this.campania = campania;
        this.id = new CentroCampaniaId(centro.getId(), campania.getId());
        this.activo = activo != null ? activo : true;
    }

    public CentroCampaniaId getId() {
        return id;
    }

    public void setId(CentroCampaniaId id) {
        this.id = id;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
