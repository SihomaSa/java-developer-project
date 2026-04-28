package com.empresa.app.dto;

import com.empresa.app.model.Producto.EstadoProducto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTOs (Data Transfer Objects) para Producto
 * Separa la capa de persistencia de la API pública
 */
public class ProductoDTO {

    // DTO para crear/actualizar
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 200)
        private String nombre;

        @NotBlank(message = "El SKU es obligatorio")
        @Pattern(regexp = "^[A-Z0-9\\-]{4,20}$")
        private String sku;

        @Size(max = 2000)
        private String descripcion;

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin("0.01")
        private BigDecimal precio;

        @Min(0)
        private Integer stock;

        private Long categoriaId;
    }

    // DTO de respuesta completa
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String nombre;
        private String sku;
        private String descripcion;
        private BigDecimal precio;
        private Integer stock;
        private EstadoProducto estado;
        private String categoriaNombre;
        private boolean tieneStock;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // DTO compacto para listados
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Long id;
        private String nombre;
        private String sku;
        private BigDecimal precio;
        private Integer stock;
        private EstadoProducto estado;
    }

    // DTO para actualizar stock
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockRequest {
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        @NotNull
        private Integer cantidad;
        @NotBlank
        private String operacion; // INCREMENTAR | REDUCIR
    }
}
