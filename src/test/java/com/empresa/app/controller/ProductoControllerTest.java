package com.empresa.app.controller;

import com.empresa.app.dto.ProductoDTO;
import com.empresa.app.exception.RecursoNoEncontradoException;
import com.empresa.app.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración del controlador REST
 * - @WebMvcTest carga solo la capa web
 * - MockMvc para simular HTTP
 * - @WithMockUser para simular autenticación
 * - Verifica JSON responses y status codes
 */
@WebMvcTest(ProductoController.class)
@DisplayName("ProductoController - Tests de Integración Web")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductoDTO.Response crearResponseMock() {
        return ProductoDTO.Response.builder()
                .id(1L)
                .nombre("Laptop Test")
                .sku("LAP-TEST-01")
                .precio(new BigDecimal("999.99"))
                .stock(10)
                .tieneStock(true)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/productos/{id} - debe retornar 200 con producto")
    void getProducto_debeRetornar200() throws Exception {
        // Given
        given(productoService.obtenerPorId(1L)).willReturn(crearResponseMock());

        // When / Then
        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Laptop Test"))
                .andExpect(jsonPath("$.sku").value("LAP-TEST-01"))
                .andExpect(jsonPath("$.precio").value(999.99));
    }

    @Test
    @DisplayName("GET /api/v1/productos/{id} - debe retornar 404 cuando no existe")
    void getProducto_debeRetornar404CuandoNoExiste() throws Exception {
        // Given
        given(productoService.obtenerPorId(99L))
                .willThrow(new RecursoNoEncontradoException("Producto", 99L));

        // When / Then
        mockMvc.perform(get("/api/v1/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/productos - debe crear y retornar 201")
    void postProducto_debeCrearYRetornar201() throws Exception {
        // Given
        ProductoDTO.Request request = ProductoDTO.Request.builder()
                .nombre("Nuevo Producto")
                .sku("NUE-PRD-01")
                .precio(new BigDecimal("199.99"))
                .stock(100)
                .build();

        given(productoService.crear(any())).willReturn(crearResponseMock());

        // When / Then
        mockMvc.perform(post("/api/v1/productos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/productos - debe retornar 400 con datos inválidos")
    void postProducto_debeRetornar400ConDatosInvalidos() throws Exception {
        // Given - Request sin nombre ni SKU (campos obligatorios)
        ProductoDTO.Request requestInvalido = new ProductoDTO.Request();
        requestInvalido.setPrecio(new BigDecimal("-5")); // precio negativo

        // When / Then
        mockMvc.perform(post("/api/v1/productos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/v1/productos/{id} - debe retornar 204")
    void deleteProducto_debeRetornar204() throws Exception {
        // Given
        willDoNothing().given(productoService).eliminar(1L);

        // When / Then
        mockMvc.perform(delete("/api/v1/productos/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L);
    }

    @Test
    @DisplayName("DELETE sin autenticación - debe retornar 401")
    void deleteProducto_sinAuth_debeRetornar401() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1").with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
