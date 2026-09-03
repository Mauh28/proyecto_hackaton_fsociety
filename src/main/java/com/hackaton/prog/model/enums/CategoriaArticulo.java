package com.hackaton.prog.model.enums;

public enum CategoriaArticulo {
    NO_PERECEDERO("no perecedero"),
    PERECEDERO("perecedero"),
    ROPA("ropa"),
    LIMPIEZA("limpieza"),
    MEDICAMENTO("medicamento"),
    OTRO("otro");

    private final String valorDb;

    CategoriaArticulo(String valorDb) {
        this.valorDb = valorDb;
    }

    public String getValorDb() {
        return valorDb;
    }

    public static CategoriaArticulo desdeValorDb(String valor) {
        for (CategoriaArticulo cat : values()) {
            if (cat.valorDb.equalsIgnoreCase(valor)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Categoría desconocida: " + valor);
    }
}
