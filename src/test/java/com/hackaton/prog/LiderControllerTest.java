package com.hackaton.prog;

import com.hackaton.prog.controller.LiderController;
import com.hackaton.prog.controller.MenuController;
import com.hackaton.prog.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class LiderControllerTest {

    @Autowired
    private LiderController liderController;

    @Autowired
    private MenuController menuController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvcLider;
    private MockMvc mockMvcMenu;

    @BeforeEach
    void setUp() {
        this.mockMvcLider = MockMvcBuilders
                .standaloneSetup(liderController)
                .setControllerAdvice(globalExceptionHandler)
                .build();

        this.mockMvcMenu = MockMvcBuilders
                .standaloneSetup(menuController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    @DisplayName("Debe resolver el contexto de sesión del Líder con campaniaId y modulos permitidos")
    void testContextoLider() throws Exception {
        mockMvcMenu.perform(get("/api/menu/contexto")
                        .param("email", "lider.emergencia@hackaton.org")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("LIDER"))
                .andExpect(jsonPath("$.campaniaId").value(1))
                .andExpect(jsonPath("$.campaniaNombre").value(containsString("Huracán")))
                .andExpect(jsonPath("$.modulosPermitidos", hasItems("lider", "campanias")));
    }

    @Test
    @DisplayName("Debe obtener el dashboard analítico de campaña para el Líder")
    void testObtenerDashboardLider() throws Exception {
        mockMvcLider.perform(get("/api/lider/dashboard")
                        .param("email", "lider.emergencia@hackaton.org")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaniaId").value(1))
                .andExpect(jsonPath("$.campaniaNombre").value(containsString("Huracán")))
                .andExpect(jsonPath("$.metaUnidades").isNumber())
                .andExpect(jsonPath("$.stockActual").isNumber())
                .andExpect(jsonPath("$.porcentajeAvance").isNumber())
                .andExpect(jsonPath("$.centrosParticipantes").isArray());
    }

    @Test
    @DisplayName("Debe permitir al Líder actualizar la meta de unidades y descripción de su campaña")
    void testActualizarCampaniaLider() throws Exception {
        String jsonPayload = """
                {
                    "metaUnidades": 6500.00,
                    "descripcion": "Meta ajustada por alta afluencia de damnificados",
                    "fechaFin": "2026-12-31"
                }
                """;

        mockMvcLider.perform(put("/api/lider/campania")
                        .param("email", "lider.emergencia@hackaton.org")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaniaId").value(1))
                .andExpect(jsonPath("$.metaUnidades").value(6500.0))
                .andExpect(jsonPath("$.descripcion").value("Meta ajustada por alta afluencia de damnificados"));
    }

    @Test
    @DisplayName("Debe permitir desvincular y volver a vincular un centro a la campaña")
    void testAsociarYDesasociarCentro() throws Exception {
        // 1. Desasociar centro 2 (existente en el seed)
        mockMvcLider.perform(delete("/api/lider/centros/2")
                        .param("email", "lider.emergencia@hackaton.org")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.centrosParticipantes[*].idCentro", not(hasItem(2))));

        // 2. Re-asociar centro 2
        String asociarJson = """
                {
                    "centroId": 2
                }
                """;

        mockMvcLider.perform(post("/api/lider/centros/asociar")
                        .param("email", "lider.emergencia@hackaton.org")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asociarJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.centrosParticipantes[*].idCentro", hasItem(2)));
    }

    @Test
    @DisplayName("Debe rechazar con 403 si un voluntario intenta consultar el dashboard de Líder")
    void testAccesoDenegadoParaVoluntario() throws Exception {
        mockMvcLider.perform(get("/api/lider/dashboard")
                        .param("email", "voluntario.central@hackaton.org")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acceso Denegado"));
    }
}
