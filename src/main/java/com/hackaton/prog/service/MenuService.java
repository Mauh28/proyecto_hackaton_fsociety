package com.hackaton.prog.service;

import com.hackaton.prog.dto.UsuarioContextoDTO;
import com.hackaton.prog.exception.AccesoDenegadoException;
import com.hackaton.prog.exception.CuentaInactivaException;
import com.hackaton.prog.exception.UsuarioNoEncontradoException;
import com.hackaton.prog.model.Usuario;
import com.hackaton.prog.model.enums.RolUsuario;
import com.hackaton.prog.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MenuService {

    private final UsuarioRepository usuarioRepository;

    public MenuService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioContextoDTO obtenerContextoValidado(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico del usuario es obligatorio para validar la sesión.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsuarioNoEncontradoException("No se encontró ningún usuario registrado con el correo: " + email));

        if (!usuario.isActivo()) {
            throw new CuentaInactivaException("La cuenta de usuario (" + email + ") está desactivada. Comuníquese con el Coordinador.");
        }

        List<String> modulosPermitidos = calcularModulosPermitidos(usuario.getRol());

        Integer centroId = usuario.getCentro() != null ? usuario.getCentro().getId() : null;
        String centroNombre = usuario.getCentro() != null ? usuario.getCentro().getNombre() : null;

        Integer institucionId = usuario.getInstitucion() != null ? usuario.getInstitucion().getId() : null;
        String institucionNombre = usuario.getInstitucion() != null ? usuario.getInstitucion().getNombre() : null;

        return new UsuarioContextoDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().name(),
                centroId,
                centroNombre,
                institucionId,
                institucionNombre,
                usuario.isActivo(),
                modulosPermitidos
        );
    }

    public boolean validarPermisoModulo(String email, String modulo) {
        if (modulo == null || modulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del módulo es obligatorio.");
        }

        UsuarioContextoDTO contexto = obtenerContextoValidado(email);
        boolean tieneAcceso = contexto.getModulosPermitidos().contains(modulo.trim().toLowerCase());

        if (!tieneAcceso) {
            throw new AccesoDenegadoException("El usuario con rol " + contexto.getRol() + 
                    " no tiene permisos para acceder al módulo: " + modulo);
        }

        return true;
    }

    private List<String> calcularModulosPermitidos(RolUsuario rol) {
        if (rol == null) {
            return Collections.emptyList();
        }

        List<String> modulos = new ArrayList<>();
        switch (rol) {
            case VOLUNTARIO:
                modulos.add("recepcion");
                break;
            case ENCARGADO:
                modulos.add("recepcion");
                modulos.add("encargado");
                break;
            case COORDINADOR:
                modulos.add("recepcion");
                modulos.add("encargado");
                modulos.add("coordinador");
                break;
            case INSTITUCION:
                modulos.add("institucion");
                break;
            case LIDER:
                modulos.add("campanias");
                break;
        }
        return Collections.unmodifiableList(modulos);
    }
}
