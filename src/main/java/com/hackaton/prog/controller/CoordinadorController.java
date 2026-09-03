package com.hackaton.prog.controller;

import com.hackaton.prog.dto.*;
import com.hackaton.prog.model.Campania;
import com.hackaton.prog.model.Centro;
import com.hackaton.prog.service.CoordinadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/coordinador")
public class CoordinadorController {

    private final CoordinadorService coordinadorService;

    public CoordinadorController(CoordinadorService coordinadorService) {
        this.coordinadorService = coordinadorService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardGlobalDTO> obtenerDashboard() {
        DashboardGlobalDTO dashboard = coordinadorService.obtenerDashboardGlobal();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/catalogos")
    public ResponseEntity<CatalogosCoordinadorDTO> obtenerCatalogos() {
        CatalogosCoordinadorDTO catalogos = coordinadorService.obtenerCatalogos();
        return ResponseEntity.ok(catalogos);
    }

    @PostMapping("/campana")
    public ResponseEntity<Map<String, Object>> guardarCampania(@RequestBody GuardarCampaniaRequest request) {
        Campania campania = coordinadorService.guardarCampania(request);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("exito", true);
        resp.put("mensaje", "Campaña '" + campania.getNombre() + "' registrada exitosamente.");
        resp.put("id", campania.getId());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/centro")
    public ResponseEntity<Map<String, Object>> registrarCentro(@RequestBody GuardarCentroRequest request) {
        Centro centro = coordinadorService.registrarCentro(request);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("exito", true);
        resp.put("mensaje", "Centro de acopio '" + centro.getNombre() + "' registrado exitosamente.");
        resp.put("id", centro.getId());
        return ResponseEntity.ok(resp);
    }
}
