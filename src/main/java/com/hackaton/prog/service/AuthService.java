package com.hackaton.prog.service;

import com.hackaton.prog.dto.LoginRequest;
import com.hackaton.prog.dto.LoginResponse;
import com.hackaton.prog.exception.CredencialesInvalidasException;
import com.hackaton.prog.model.Usuario;
import com.hackaton.prog.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new CredencialesInvalidasException("El correo y la contraseña son obligatorios");
        }

        String emailLimpio = request.getEmail().trim().toLowerCase();
        String password = request.getPassword();

        Usuario usuario = usuarioRepository.findByEmail(emailLimpio)
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales incorrectas o usuario no encontrado"));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new CredencialesInvalidasException("La cuenta de usuario se encuentra inactiva");
        }

        // Validación de contraseña (plana o hash)
        if (!usuario.getPassword().equals(password)) {
            throw new CredencialesInvalidasException("Credenciales incorrectas o usuario no encontrado");
        }

        Integer centroId = usuario.getCentro() != null ? usuario.getCentro().getId() : null;
        String centroNombre = usuario.getCentro() != null ? usuario.getCentro().getNombre() : null;
        Integer institucionId = usuario.getInstitucion() != null ? usuario.getInstitucion().getId() : null;
        String institucionNombre = usuario.getInstitucion() != null ? usuario.getInstitucion().getNombre() : null;

        return new LoginResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                centroId,
                centroNombre,
                institucionId,
                institucionNombre
        );
    }
}
