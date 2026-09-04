package com.hackaton.prog.controller;

import com.hackaton.prog.dto.ArticuloItemDTO;
import com.hackaton.prog.dto.RecepcionRequestDTO;
import com.hackaton.prog.dto.RecepcionResponseDTO;
import com.hackaton.prog.dto.ResumenRecepcionDTO;
import com.hackaton.prog.service.RecepcionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepcion")
public class RecepcionController {

    private final RecepcionService recepcionService;

    public RecepcionController(RecepcionService recepcionService) {
        this.recepcionService = recepcionService;
    }

    /**
     * Endpoint para registrar donación con persistencia atómica vía JPA en TiDB Cloud.
     */
    @PostMapping
    public ResponseEntity<RecepcionResponseDTO> registrarDonacion(@RequestBody RecepcionRequestDTO request) {
        RecepcionResponseDTO response = recepcionService.registrarRecepcion(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obtener el catálogo de artículos filtrados por categoría.
     */
    @GetMapping("/articulos")
    public ResponseEntity<List<ArticuloItemDTO>> listarArticulos(
            @RequestParam(value = "categoria", required = false) String categoria) {
        List<ArticuloItemDTO> articulos = recepcionService.listarArticulos(categoria);
        return ResponseEntity.ok(articulos);
    }

    /**
     * Endpoint para obtener el resumen del centro y campaña activa en tiempo real.
     */
    @GetMapping("/resumen")
    public ResponseEntity<ResumenRecepcionDTO> obtenerResumen(
            @RequestParam(value = "centroId", required = false) Integer centroId,
            @RequestParam(value = "campaniaId", required = false) Integer campaniaId) {
        ResumenRecepcionDTO resumen = recepcionService.obtenerResumenCentro(centroId, campaniaId);
        return ResponseEntity.ok(resumen);
    }
}
