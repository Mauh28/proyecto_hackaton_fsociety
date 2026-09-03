package com.hackaton.prog.model.enums;

public enum EstadoTransferencia {
    PENDIENTE("pendiente"),
    COMPLETADA("completada"),
    CANCELADA("cancelada");

    private final String valorDb;

    EstadoTransferencia(String valorDb) {
        this.valorDb = valorDb;
    }

    public String getValorDb() {
        return valorDb;
    }

    public static EstadoTransferencia desdeValorDb(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        String clean = valor.trim();
        String normalizado = clean.replace(" ", "_").replace("-", "_").toUpperCase();
        for (EstadoTransferencia est : values()) {
            if (est.name().equalsIgnoreCase(normalizado) || 
                est.valorDb.equalsIgnoreCase(clean)) {
                return est;
            }
        }
        throw new IllegalArgumentException("Estado de transferencia desconocido: " + valor);
    }
}
