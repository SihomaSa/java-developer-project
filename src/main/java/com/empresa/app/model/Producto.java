package com.empresa.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Producto con:
 * - Hibernate/JPA (persistencia y mapeo ORM)
 * - Jakarta EE (validaciones Bean Validation)
 * - Lombok (reducción de boilerplate)
 */
@Entity
@Table(name = "productos", indexes = {
        @Index(name = "idx_producto_sku", columnList = "sku", unique = true),
        @Index(name = "idx_producto_categoria", columnList = "categoria_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    @Column(nullable = false, length = 200)
    private String nombre;

    @NotBlank(message = "El SKU es obligatorio")
    @Pattern(regexp = "^[A-Z0-9\\-]{4,20}$", message = "El SKU debe ser alfanumérico con guiones (4-20 chars)")
    @Column(nullable = false, unique = true, length = 20)
    private String sku;

    @Size(max = 2000)
    @Column(length = 2000)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El precio no puede superar 999,999.99")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock = 0;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProducto estado = EstadoProducto.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Integer version;

    // Lógica de negocio en el modelo
    public boolean tieneStock() {
        return stock != null && stock > 0;
    }

    public boolean estaActivo() {
        return EstadoProducto.ACTIVO.equals(estado);
    }

    public void reducirStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser positiva");
        if (this.stock < cantidad) throw new IllegalStateException("Stock insuficiente: disponible=" + this.stock);
        this.stock -= cantidad;
    }

    public enum EstadoProducto {
        ACTIVO, INACTIVO, AGOTADO, DESCONTINUADO
    }
}
