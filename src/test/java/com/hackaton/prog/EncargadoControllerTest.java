package com.hackaton.prog;

import com.hackaton.prog.controller.EncargadoController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EncargadoControllerTest {

    @Autowired
    private EncargadoController encargadoController;

    @Test
    @DisplayName("Debe obtener catalogos para el centro 1 sin errores")
    void testObtenerCatalogos() {
        assertNotNull(encargadoController, "El controlador de encargado debe existir");
        var response = encargadoController.obtenerCatalogos(1);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getArticulos().isEmpty(), "Debe haber artículos cargados en los catálogos");
    }

    @Test
    @DisplayName("Debe obtener dashboard para el centro 1 sin errores")
    void testObtenerDashboard() {
        assertNotNull(encargadoController, "El controlador de encargado debe existir");
        var response = encargadoController.obtenerDashboard(1);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        System.out.println(">>> Dashboard Centro Nombre: " + response.getBody().getCentroNombre());
    }
}
