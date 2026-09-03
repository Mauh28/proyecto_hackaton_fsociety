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
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        String clean = valor.trim();
        String normalizado = clean.replace(" ", "_").replace("-", "_")
                .replace("Í", "I").replace("í", "i")
                .toUpperCase();
        for (RolUsuario rol : values()) {
            if (rol.name().equalsIgnoreCase(normalizado) || 
                rol.valorDb.equalsIgnoreCase(clean)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol de usuario desconocido: " + valor);
    }
}
