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
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        String clean = valor.trim();
        String normalizado = clean.replace(" ", "_").replace("-", "_").toUpperCase();
        for (UnidadMedida u : values()) {
            if (u.name().equalsIgnoreCase(normalizado) || 
                u.valorDb.equalsIgnoreCase(clean)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Unidad de medida desconocida: " + valor);
    }
}
