package com.hackaton.prog.model.enums;

public enum UnidadMedida {
    PIEZA("pieza"),
    KG("kg"),
    L("l"),
    BOLSA("bolsa"),
    CAJA("caja");

    private final String valorDb;

    UnidadMedida(String valorDb) {
        this.valorDb = valorDb;
    }

    public String getValorDb() {
        return valorDb;
    }

    public static UnidadMedida desdeValorDb(String valor) {
        for (UnidadMedida u : values()) {
            if (u.valorDb.equalsIgnoreCase(valor)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Unidad de medida desconocida: " + valor);
    }
}
