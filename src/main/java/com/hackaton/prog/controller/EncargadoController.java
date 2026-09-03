package com.hackaton.prog.controller;

import com.hackaton.prog.dto.CatalogosEncargadoDTO;
import com.hackaton.prog.dto.DashboardCentroDTO;
import com.hackaton.prog.dto.RegistroMovimientoRequest;
import com.hackaton.prog.service.EncargadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/encargado")
public class EncargadoController {

    private final EncargadoService encargadoService;

    public EncargadoController(EncargadoService encargadoService) {
        this.encargadoService = encargadoService;
    }

    @GetMapping("/catalogos")
    public ResponseEntity<CatalogosEncargadoDTO> obtenerCatalogos(@RequestParam(name = "centroId", defaultValue = "1") Integer centroId) {
        CatalogosEncargadoDTO catalogos = encargadoService.obtenerCatalogos(centroId);
        return ResponseEntity.ok(catalogos);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardCentroDTO> obtenerDashboard(@RequestParam(name = "centroId", defaultValue = "1") Integer centroId) {
        DashboardCentroDTO dashboard = encargadoService.obtenerDashboardCentro(centroId);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/movimiento")
    public ResponseEntity<Map<String, Object>> registrarMovimiento(@RequestBody RegistroMovimientoRequest request) {
        Object resultado = encargadoService.registrarMovimiento(request);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Movimiento registrado con éxito en el sistema");
        respuesta.put("exito", true);
        return ResponseEntity.ok(respuesta);
    }
}
