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
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        String clean = valor.trim();
        String normalizado = clean.replace(" ", "_").replace("-", "_").toUpperCase();
        for (CategoriaArticulo cat : values()) {
            if (cat.name().equalsIgnoreCase(normalizado) || 
                cat.valorDb.equalsIgnoreCase(clean) ||
                cat.valorDb.replace(" ", "_").equalsIgnoreCase(normalizado)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Categoría desconocida: " + valor);
    }
}
