package com.hackaton.prog.model.enums;

public enum TipoMovimiento {
    RECEPCION("recepcion"),
    ENTREGA("entrega"),
    MERMA("merma"),
    TRANSFERENCIA_SALIDA("transferencia_salida"),
    TRANSFERENCIA_ENTRADA("transferencia_entrada"),
    AJUSTE_POSITIVO("ajuste_positivo"),
    AJUSTE_NEGATIVO("ajuste_negativo");

    private final String valorDb;

    TipoMovimiento(String valorDb) {
        this.valorDb = valorDb;
    }

    public String getValorDb() {
        return valorDb;
    }

    public boolean esEntrada() {
        return this == RECEPCION || this == TRANSFERENCIA_ENTRADA || this == AJUSTE_POSITIVO;
    }

    public boolean esSalida() {
        return this == ENTREGA || this == MERMA || this == TRANSFERENCIA_SALIDA || this == AJUSTE_NEGATIVO;
    }

    public static TipoMovimiento desdeValorDb(String valor) {
        for (TipoMovimiento tipo : values()) {
            if (tipo.valorDb.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de movimiento desconocido: " + valor);
    }
}
