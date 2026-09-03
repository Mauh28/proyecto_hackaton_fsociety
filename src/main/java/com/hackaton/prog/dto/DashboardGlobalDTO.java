package com.hackaton.prog.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardGlobalDTO {

    private BigDecimal stockGlobal;
    private BigDecimal mermaTotal;
    private long centrosActivos;
    private String articuloMasDonado;
    private String campaniaNombre;
    private BigDecimal metaCampania;
    private List<CentroComparativaDTO> centros = new ArrayList<>();

    public DashboardGlobalDTO() {
    }

    public DashboardGlobalDTO(BigDecimal stockGlobal, BigDecimal mermaTotal, long centrosActivos,
                              String articuloMasDonado, String campaniaNombre, BigDecimal metaCampania,
                              List<CentroComparativaDTO> centros) {
        this.stockGlobal = stockGlobal != null ? stockGlobal : BigDecimal.ZERO;
        this.mermaTotal = mermaTotal != null ? mermaTotal : BigDecimal.ZERO;
        this.centrosActivos = centrosActivos;
        this.articuloMasDonado = articuloMasDonado != null ? articuloMasDonado : "N/A";
        this.campaniaNombre = campaniaNombre != null ? campaniaNombre : "Sin Campaña";
        this.metaCampania = metaCampania != null ? metaCampania : BigDecimal.ZERO;
        this.centros = centros != null ? centros : new ArrayList<>();
    }

    public BigDecimal getStockGlobal() {
        return stockGlobal;
    }

    public void setStockGlobal(BigDecimal stockGlobal) {
        this.stockGlobal = stockGlobal;
    }

    public BigDecimal getMermaTotal() {
        return mermaTotal;
    }

    public void setMermaTotal(BigDecimal mermaTotal) {
        this.mermaTotal = mermaTotal;
    }

    public long getCentrosActivos() {
        return centrosActivos;
    }

    public void setCentrosActivos(long centrosActivos) {
        this.centrosActivos = centrosActivos;
    }

    public String getArticuloMasDonado() {
        return articuloMasDonado;
    }

    public void setArticuloMasDonado(String articuloMasDonado) {
        this.articuloMasDonado = articuloMasDonado;
    }

    public String getCampaniaNombre() {
        return campaniaNombre;
    }

    public void setCampaniaNombre(String campaniaNombre) {
        this.campaniaNombre = campaniaNombre;
    }

    public BigDecimal getMetaCampania() {
        return metaCampania;
    }

    public void setMetaCampania(BigDecimal metaCampania) {
        this.metaCampania = metaCampania;
    }

    public List<CentroComparativaDTO> getCentros() {
        return centros;
    }

    public void setCentros(List<CentroComparativaDTO> centros) {
        this.centros = centros;
    }
}
