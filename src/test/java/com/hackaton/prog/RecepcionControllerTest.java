package com.hackaton.prog;

import com.hackaton.prog.controller.RecepcionController;
import com.hackaton.prog.dto.RecepcionRequestDTO;
import com.hackaton.prog.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class RecepcionControllerTest {

    @Autowired
    private RecepcionController recepcionController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(recepcionController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    @DisplayName("Debe consultar resumen del centro exitosamente")
    void testObtenerResumenCentro() throws Exception {
        mockMvc.perform(get("/api/recepcion/resumen")
                        .param("centroId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.centroId").value(1))
                .andExpect(jsonPath("$.centroNombre").isNotEmpty())
                .andExpect(jsonPath("$.metaTotal").isNumber());
    }

    @Test
    @DisplayName("Debe listar artículos del catálogo")
    void testListarArticulosCatalogo() throws Exception {
        mockMvc.perform(get("/api/recepcion/articulos")
                        .param("categoria", "NO_PERECEDERO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe rechazar con 400 si la cantidad recibida es negativa o cero")
    void testRechazarCantidadInvalida() throws Exception {
        String jsonPayload = """
                {
                    "centroId": 1,
                    "campaniaId": 1,
                    "articuloId": 1,
                    "cantidad": -10.00,
                    "usuarioId": 3,
                    "esAnonimo": true
                }
                """;

        mockMvc.perform(post("/api/recepcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Solicitud Inválida"))
                .andExpect(jsonPath("$.mensaje").isNotEmpty());
    }

    @Test
    @DisplayName("Debe rechazar con 400 si no se indica ni selecciona un artículo")
    void testRechazarSinArticulo() throws Exception {
        String jsonPayload = """
                {
                    "centroId": 1,
                    "campaniaId": 1,
                    "articuloId": null,
                    "articuloNombre": "",
                    "cantidad": 5.00,
                    "usuarioId": 3,
                    "esAnonimo": true
                }
                """;

        mockMvc.perform(post("/api/recepcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Solicitud Inválida"));
    }

    @Test
    @DisplayName("Debe registrar donación exitosamente y persistir en base de datos")
    void testRegistrarRecepcionExitoso() throws Exception {
        String jsonPayload = """
                {
                    "centroId": 1,
                    "campaniaId": 1,
                    "articuloNombre": "Agua embotellada 1L",
                    "categoria": "NO_PERECEDERO",
                    "unidad": "PIEZA",
                    "cantidad": 5.00,
                    "usuarioId": 3,
                    "esAnonimo": false,
                    "donanteNombre": "Donante Test"
                }
                """;

        mockMvc.perform(post("/api/recepcion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.movimientoId").isNumber())
                .andExpect(jsonPath("$.stockActual").isNumber());
    }
}
