package com.hackaton.prog.controller;

import com.hackaton.prog.dto.ActualizarCampaniaLiderRequest;
import com.hackaton.prog.dto.AsociarCentroCampaniaRequest;
import com.hackaton.prog.dto.DashboardLiderDTO;
import com.hackaton.prog.service.LiderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lider")
public class LiderController {

    private final LiderService liderService;

    public LiderController(LiderService liderService) {
        this.liderService = liderService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardLiderDTO> obtenerDashboard(
            @RequestParam(name = "usuarioId", required = false) Integer usuarioId,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "campaniaId", required = false) Integer campaniaId) {
        DashboardLiderDTO dashboard = liderService.obtenerDashboard(usuarioId, email, campaniaId);
        return ResponseEntity.ok(dashboard);
    }

    @PutMapping("/campania")
    public ResponseEntity<DashboardLiderDTO> actualizarCampania(
            @RequestParam(name = "usuarioId", required = false) Integer usuarioId,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "campaniaId", required = false) Integer campaniaId,
            @RequestBody ActualizarCampaniaLiderRequest request) {
        DashboardLiderDTO dashboard = liderService.actualizarCampania(usuarioId, email, campaniaId, request);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/centros/asociar")
    public ResponseEntity<DashboardLiderDTO> asociarCentro(
            @RequestParam(name = "usuarioId", required = false) Integer usuarioId,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "campaniaId", required = false) Integer campaniaId,
            @RequestBody AsociarCentroCampaniaRequest request) {
        DashboardLiderDTO dashboard = liderService.asociarCentro(usuarioId, email, campaniaId, request.getCentroId());
        return ResponseEntity.ok(dashboard);
    }

    @DeleteMapping("/centros/{centroId}")
    public ResponseEntity<DashboardLiderDTO> desasociarCentro(
            @PathVariable Integer centroId,
            @RequestParam(name = "usuarioId", required = false) Integer usuarioId,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "campaniaId", required = false) Integer campaniaId) {
        DashboardLiderDTO dashboard = liderService.desasociarCentro(usuarioId, email, campaniaId, centroId);
        return ResponseEntity.ok(dashboard);
    }
}
