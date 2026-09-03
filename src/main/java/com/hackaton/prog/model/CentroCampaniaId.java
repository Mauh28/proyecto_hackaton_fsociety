package com.hackaton.prog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CentroCampaniaId implements Serializable {

    @Column(name = "id_centro")
    private Integer idCentro;

    @Column(name = "id_campania")
    private Integer idCampania;

    public CentroCampaniaId() {
    }

    public CentroCampaniaId(Integer idCentro, Integer idCampania) {
        this.idCentro = idCentro;
        this.idCampania = idCampania;
    }

    public Integer getIdCentro() {
        return idCentro;
    }

    public void setIdCentro(Integer idCentro) {
        this.idCentro = idCentro;
    }

    public Integer getIdCampania() {
        return idCampania;
    }

    public void setIdCampania(Integer idCampania) {
        this.idCampania = idCampania;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CentroCampaniaId that = (CentroCampaniaId) o;
        return Objects.equals(idCentro, that.idCentro) && Objects.equals(idCampania, that.idCampania);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCentro, idCampania);
    }
}
