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
        if (valor == null) return null;
        for (MotivoMovimiento m : values()) {
            if (m.valorDb.equalsIgnoreCase(valor)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Motivo desconocido: " + valor);
    }
}
