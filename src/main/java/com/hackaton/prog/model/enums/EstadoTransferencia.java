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
        for (EstadoTransferencia est : values()) {
            if (est.valorDb.equalsIgnoreCase(valor)) {
                return est;
            }
        }
        throw new IllegalArgumentException("Estado de transferencia desconocido: " + valor);
    }
}
