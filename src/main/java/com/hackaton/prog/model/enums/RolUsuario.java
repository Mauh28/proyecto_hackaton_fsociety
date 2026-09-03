package com.hackaton.prog.model.enums;

public enum RolUsuario {
    COORDINADOR("Coordinador"),
    ENCARGADO("Encargado"),
    VOLUNTARIO("Voluntario"),
    INSTITUCION("Institucion"),
    LIDER("Lider");

    private final String valorDb;

    RolUsuario(String valorDb) {
        this.valorDb = valorDb;
    }

    public String getValorDb() {
        return valorDb;
    }

    public static RolUsuario desdeValorDb(String valor) {
        for (RolUsuario rol : values()) {
            if (rol.valorDb.equalsIgnoreCase(valor)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol de usuario desconocido: " + valor);
    }
}
