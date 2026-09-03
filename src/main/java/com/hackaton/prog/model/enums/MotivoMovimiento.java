package com.hackaton.prog.model.enums;

public enum MotivoMovimiento {
    CADUCIDAD("caducidad"),
    DANO("daño"),
    PERDIDA("perdida"),
    CORRECCION_CONTEO("correccion_conteo"),
    ERROR_CAPTURA("error_captura"),
    OTRO("otro");

    private final String valorDb;

    MotivoMovimiento(String valorDb) {
        this.valorDb = valorDb;
    }

    public String getValorDb() {
        return valorDb;
    }

    public static MotivoMovimiento desdeValorDb(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        String clean = valor.trim();
        String normalizado = clean.replace(" ", "_").replace("-", "_")
                .replace("Ñ", "N").replace("ñ", "n")
                .replace("Á", "A").replace("á", "a")
                .replace("É", "E").replace("é", "e")
                .replace("Í", "I").replace("í", "i")
                .replace("Ó", "O").replace("ó", "o")
                .replace("Ú", "U").replace("ú", "u")
                .toUpperCase();
        for (MotivoMovimiento m : values()) {
            if (m.name().equalsIgnoreCase(normalizado) || 
                m.valorDb.equalsIgnoreCase(clean) ||
                m.valorDb.replace(" ", "_").equalsIgnoreCase(clean)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Motivo desconocido: " + valor);
    }
}
