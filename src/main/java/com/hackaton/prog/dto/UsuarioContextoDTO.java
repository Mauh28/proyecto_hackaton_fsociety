package com.hackaton.prog.dto;

import java.util.List;

public class UsuarioContextoDTO {

    private Integer id;
    private String nombre;
    private String email;
    private String rol;
    private Integer centroId;
    private String centroNombre;
    private Integer institucionId;
    private String institucionNombre;
    private boolean activo;
    private List<String> modulosPermitidos;

    public UsuarioContextoDTO() {
    }

    public UsuarioContextoDTO(Integer id, String nombre, String email, String rol, Integer centroId,
                              String centroNombre, Integer institucionId, String institucionNombre,
                              boolean activo, List<String> modulosPermitidos) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.centroId = centroId;
        this.centroNombre = centroNombre;
        this.institucionId = institucionId;
        this.institucionNombre = institucionNombre;
        this.activo = activo;
        this.modulosPermitidos = modulosPermitidos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Integer getCentroId() {
        return centroId;
    }

    public void setCentroId(Integer centroId) {
        this.centroId = centroId;
    }

    public String getCentroNombre() {
        return centroNombre;
    }

    public void setCentroNombre(String centroNombre) {
        this.centroNombre = centroNombre;
    }

    public Integer getInstitucionId() {
        return institucionId;
    }

    public void setInstitucionId(Integer institucionId) {
        this.institucionId = institucionId;
    }

    public String getInstitucionNombre() {
        return institucionNombre;
    }

    public void setInstitucionNombre(String institucionNombre) {
        this.institucionNombre = institucionNombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<String> getModulosPermitidos() {
        return modulosPermitidos;
    }

    public void setModulosPermitidos(List<String> modulosPermitidos) {
        this.modulosPermitidos = modulosPermitidos;
    }
}
