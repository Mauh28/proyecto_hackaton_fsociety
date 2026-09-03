package com.hackaton.prog.model;

import com.hackaton.prog.model.enums.RolUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_id")
    private Centro centro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institucion_id")
    private InstitucionReceptora institucion;

    @Column(nullable = false)
    private RolUsuario rol;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String password, Centro centro, InstitucionReceptora institucion, RolUsuario rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.centro = centro;
        this.institucion = institucion;
        this.rol = rol;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Centro getCentro() {
        return centro;
    }

    public void setCentro(Centro centro) {
        this.centro = centro;
    }

    public InstitucionReceptora getInstitucion() {
        return institucion;
    }

    public void setInstitucion(InstitucionReceptora institucion) {
        this.institucion = institucion;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}
