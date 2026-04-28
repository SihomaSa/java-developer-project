package com.empresa.app.controller;

import com.empresa.app.dto.ProductoDTO;
import com.empresa.app.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST API para Productos
 * - MVC con ResponseEntity tipado
 * - Validaciones con Jakarta EE
 * - Paginación y filtrado
 * - Swagger/OpenAPI documentado
 * - Seguridad por roles
 */
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para gestión de productos del catálogo")
public class ProductoController {

    private final ProductoService productoService;

    // ==================== GET ====================

    @GetMapping
    @Operation(summary = "Listar productos activos", description = "Retorna página de productos activos con paginación")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public ResponseEntity<Page<ProductoDTO.Summary>> listarActivos(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(productoService.listarActivos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoDTO.Response> obtenerPorId(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Obtener producto por SKU")
    public ResponseEntity<ProductoDTO.Response> obtenerPorSku(@PathVariable String sku) {
        return ResponseEntity.ok(productoService.obtenerPorSku(sku));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por texto")
    public ResponseEntity<Page<ProductoDTO.Summary>> buscar(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productoService.buscar(q, pageable));
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Productos con stock bajo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INVENTARIO')")
    public ResponseEntity<List<ProductoDTO.Summary>> stockBajo(
            @RequestParam(defaultValue = "5") int umbral) {
        return ResponseEntity.ok(productoService.obtenerConStockBajo(umbral));
    }

    @GetMapping("/categorias/{categoriaId}/precio-promedio")
    @Operation(summary = "Precio promedio de una categoría")
    public ResponseEntity<BigDecimal> precioPromedio(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.obtenerPrecioPromedioPorCategoria(categoriaId));
    }

    // ==================== POST ====================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nuevo producto")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCTOS')")
    public ResponseEntity<ProductoDTO.Response> crear(
            @Valid @RequestBody ProductoDTO.Request request) {
        ProductoDTO.Response creado = productoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // ==================== PUT ====================

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto completo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCTOS')")
    public ResponseEntity<ProductoDTO.Response> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO.Request request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @PutMapping("/{id}/stock")
    @Operation(summary = "Actualizar stock del producto")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INVENTARIO')")
    public ResponseEntity<ProductoDTO.Response> actualizarStock(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO.StockRequest stockRequest) {
        return ResponseEntity.ok(productoService.actualizarStock(id, stockRequest));
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar producto")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== OPERACIONES MASIVAS ====================

    @PostMapping("/marcar-agotados")
    @Operation(summary = "Marcar como agotados los productos con stock 0")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> marcarAgotados() {
        int total = productoService.marcarAgotados();
        return ResponseEntity.ok("Se marcaron " + total + " productos como agotados");
    }
}
