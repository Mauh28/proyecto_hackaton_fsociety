package com.hackaton.prog.controller;

import com.hackaton.prog.dto.UsuarioContextoDTO;
import com.hackaton.prog.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/contexto")
    public ResponseEntity<UsuarioContextoDTO> obtenerContexto(@RequestParam String email) {
        UsuarioContextoDTO contexto = menuService.obtenerContextoValidado(email);
        return ResponseEntity.ok(contexto);
    }

    @GetMapping("/validar-acceso")
    public ResponseEntity<Map<String, Object>> validarAcceso(
            @RequestParam String email,
            @RequestParam String modulo) {
        boolean autorizado = menuService.validarPermisoModulo(email, modulo);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("autorizado", autorizado);
        respuesta.put("modulo", modulo);
        respuesta.put("email", email);
        return ResponseEntity.ok(respuesta);
    }
}
