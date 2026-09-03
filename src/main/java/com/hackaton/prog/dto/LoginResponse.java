package com.hackaton.prog.dto;

import com.hackaton.prog.model.enums.RolUsuario;

public class LoginResponse {

    private Integer id;
    private String nombre;
    private String email;
    private RolUsuario rol;
    private Integer centroId;
    private String centroNombre;
    private Integer institucionId;
    private String institucionNombre;

    public LoginResponse() {
    }

    public LoginResponse(Integer id, String nombre, String email, RolUsuario rol,
                         Integer centroId, String centroNombre,
                         Integer institucionId, String institucionNombre) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.centroId = centroId;
        this.centroNombre = centroNombre;
        this.institucionId = institucionId;
        this.institucionNombre = institucionNombre;
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

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
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
}
