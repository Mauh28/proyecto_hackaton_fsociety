package com.hackaton.prog;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class MenuControllerTest {

    @Autowired
    private MenuController menuController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(menuController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    @DisplayName("Debe obtener contexto completo y 3 módulos para rol COORDINADOR")
    void testContextoCoordinador() throws Exception {
        mockMvc.perform(get("/api/menu/contexto")
                        .param("email", "coordinador@hackaton.org")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Admin Coordinadora"))
                .andExpect(jsonPath("$.rol").value("COORDINADOR"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.modulosPermitidos", hasItems("recepcion", "encargado", "coordinador")));
    }

    @Test
    @DisplayName("Debe obtener contexto y solo módulo 'recepcion' para rol VOLUNTARIO")
    void testContextoVoluntario() throws Exception {
        mockMvc.perform(get("/api/menu/contexto")
                        .param("email", "voluntario.central@hackaton.org")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Voluntario Campus Central"))
                .andExpect(jsonPath("$.rol").value("VOLUNTARIO"))
                .andExpect(jsonPath("$.modulosPermitidos", hasItem("recepcion")))
                .andExpect(jsonPath("$.modulosPermitidos", not(hasItem("coordinador"))));
    }

    @Test
    @DisplayName("Debe retornar 404 si el usuario no existe")
    void testContextoUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/api/menu/contexto")
                        .param("email", "usuario.fantasma@inexistente.org")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Usuario No Encontrado"));
    }

    @Test
    @DisplayName("Debe autorizar acceso cuando el rol posee el módulo")
    void testValidarAccesoPermitido() throws Exception {
        mockMvc.perform(get("/api/menu/validar-acceso")
                        .param("email", "coordinador@hackaton.org")
                        .param("modulo", "coordinador")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autorizado").value(true))
                .andExpect(jsonPath("$.modulo").value("coordinador"));
    }

    @Test
    @DisplayName("Debe rechazar con 403 si un voluntario intenta entrar al módulo de coordinador")
    void testValidarAccesoDenegado() throws Exception {
        mockMvc.perform(get("/api/menu/validar-acceso")
                        .param("email", "voluntario.central@hackaton.org")
                        .param("modulo", "coordinador")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acceso Denegado"));
    }
}
