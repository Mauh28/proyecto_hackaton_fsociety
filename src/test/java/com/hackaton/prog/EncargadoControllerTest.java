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
        var historial = response.getBody().getHistorial();
        assertNotNull(historial);
        if (historial.size() >= 2) {
            assertTrue(historial.get(0).getId() > historial.get(1).getId(),
                    "El primer movimiento debe tener un ID mayor que el segundo (orden descendente, más reciente primero)");
        }
    }

    @Test
    @DisplayName("Debe registrar entrega hacia beneficiario directo correctamente")
    void testRegistrarEntregaBeneficiarioDirecto() {
        var req = new com.hackaton.prog.dto.RegistroMovimientoRequest();
        req.setTipo("ENTREGA");
        req.setTipoEntrega("beneficiario");
        req.setCentroId(1);
        req.setCampaniaId(1);
        req.setArticuloId(1);
        req.setCantidad(java.math.BigDecimal.valueOf(1.0));
        req.setUsuarioId(2);
        req.setBeneficiarioNombre("Familia Prueba Flujo 6.2");

        var response = encargadoController.registrarMovimiento(req);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue((Boolean) response.getBody().get("exito"));
    }
}
