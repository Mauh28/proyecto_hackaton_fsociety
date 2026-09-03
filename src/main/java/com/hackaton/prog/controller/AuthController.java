package com.hackaton.prog.controller;

import com.hackaton.prog.dto.LoginRequest;
import com.hackaton.prog.dto.UsuarioContextoDTO;
import com.hackaton.prog.exception.CredencialesInvalidasException;
import com.hackaton.prog.exception.CuentaInactivaException;
import com.hackaton.prog.model.Usuario;
import com.hackaton.prog.repository.UsuarioRepository;
import com.hackaton.prog.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final MenuService menuService;

    public AuthController(UsuarioRepository usuarioRepository, MenuService menuService) {
        this.usuarioRepository = usuarioRepository;
        this.menuService = menuService;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioContextoDTO> login(@RequestBody LoginRequest req) {
        if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (req.getPassword() == null || req.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        String emailClean = req.getEmail().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(emailClean)
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas. Correo no registrado."));

        if (!usuario.isActivo()) {
            throw new CuentaInactivaException("Esta cuenta de usuario se encuentra desactivada.");
        }

        if (!usuario.getPassword().equals(req.getPassword())) {
            throw new CredencialesInvalidasException("Contraseña incorrecta.");
        }

        UsuarioContextoDTO contexto = menuService.obtenerContextoValidado(usuario.getEmail());
        return ResponseEntity.ok(contexto);
    }
}
