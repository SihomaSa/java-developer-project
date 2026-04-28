package com.empresa.app.service;

import com.empresa.app.dto.ProductoDTO;
import com.empresa.app.exception.RecursoNoEncontradoException;
import com.empresa.app.exception.ReglaDeNegocioException;
import com.empresa.app.model.Categoria;
import com.empresa.app.model.Producto;
import com.empresa.app.model.Producto.EstadoProducto;
import com.empresa.app.repository.CategoriaRepository;
import com.empresa.app.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de negocio para Productos
 * - Lógica de negocio desacoplada del controlador
 * - Transacciones declarativas (@Transactional)
 * - Integración con mensajería (RabbitMQ) para eventos
 * - Logging estructurado
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MensajeriaService mensajeriaService;

    /**
     * Obtiene lista paginada de productos activos
     */
    public Page<ProductoDTO.Summary> listarActivos(Pageable pageable) {
        log.debug("Listando productos activos, pageable={}", pageable);
        return productoRepository.findByEstado(EstadoProducto.ACTIVO, pageable)
                .map(this::toSummaryDTO);
    }

    /**
     * Obtiene producto por ID con su categoría (evita N+1 con JOIN FETCH)
     */
    public ProductoDTO.Response obtenerPorId(Long id) {
        log.debug("Buscando producto id={}", id);
        Producto producto = productoRepository.findByIdWithCategoria(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        return toResponseDTO(producto);
    }

    /**
     * Obtiene producto por SKU
     */
    public ProductoDTO.Response obtenerPorSku(String sku) {
        Producto producto = productoRepository.findBySku(sku)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con SKU " + sku + " no encontrado"));
        return toResponseDTO(producto);
    }

    /**
     * Búsqueda de texto libre con paginación
     */
    public Page<ProductoDTO.Summary> buscar(String texto, Pageable pageable) {
        log.info("Búsqueda de productos con texto='{}'", texto);
        return productoRepository.buscarPorTexto(texto, pageable).map(this::toSummaryDTO);
    }

    /**
     * Productos con stock por debajo del umbral
     */
    public List<ProductoDTO.Summary> obtenerConStockBajo(int umbral) {
        return productoRepository.findProductosConStockBajo(umbral)
                .stream()
                .map(this::toSummaryDTO)
                .toList();
    }

    /**
     * Precio promedio por categoría
     */
    public BigDecimal obtenerPrecioPromedioPorCategoria(Long categoriaId) {
        return productoRepository.findPrecioPromedioPorCategoria(categoriaId)
                .orElse(BigDecimal.ZERO);
    }

    // ==================== OPERACIONES DE ESCRITURA ====================

    @Transactional
    public ProductoDTO.Response crear(ProductoDTO.Request request) {
        log.info("Creando producto con SKU={}", request.getSku());

        validarSkuUnico(request.getSku(), null);

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .sku(request.getSku())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .estado(EstadoProducto.ACTIVO)
                .build();

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", request.getCategoriaId()));
            producto.setCategoria(categoria);
        }

        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado id={}, sku={}", guardado.getId(), guardado.getSku());

        // Enviar evento asíncrono a RabbitMQ
        mensajeriaService.publicarEventoProducto("PRODUCTO_CREADO", guardado.getId(), guardado.getSku());

        return toResponseDTO(guardado);
    }

    @Transactional
    public ProductoDTO.Response actualizar(Long id, ProductoDTO.Request request) {
        log.info("Actualizando producto id={}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));

        validarSkuUnico(request.getSku(), id);

        producto.setNombre(request.getNombre());
        producto.setSku(request.getSku());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", request.getCategoriaId()));
            producto.setCategoria(categoria);
        }

        Producto actualizado = productoRepository.save(producto);
        return toResponseDTO(actualizado);
    }

    @Transactional
    public ProductoDTO.Response actualizarStock(Long id, ProductoDTO.StockRequest stockRequest) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));

        switch (stockRequest.getOperacion().toUpperCase()) {
            case "INCREMENTAR" -> producto.setStock(producto.getStock() + stockRequest.getCantidad());
            case "REDUCIR" -> producto.reducirStock(stockRequest.getCantidad());
            default -> throw new ReglaDeNegocioException("Operación inválida: " + stockRequest.getOperacion());
        }

        if (producto.getStock() == 0) {
            producto.setEstado(EstadoProducto.AGOTADO);
            mensajeriaService.publicarEventoProducto("PRODUCTO_AGOTADO", producto.getId(), producto.getSku());
        }

        return toResponseDTO(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto", id);
        }
        productoRepository.deleteById(id);
        log.info("Producto eliminado id={}", id);
    }

    @Transactional
    public int marcarAgotados() {
        int actualizados = productoRepository.marcarAgotados();
        log.info("Marcados como agotados: {} productos", actualizados);
        return actualizados;
    }

    // ==================== HELPERS PRIVADOS ====================

    private void validarSkuUnico(String sku, Long idExcluir) {
        productoRepository.findBySku(sku).ifPresent(p -> {
            if (idExcluir == null || !p.getId().equals(idExcluir)) {
                throw new ReglaDeNegocioException("Ya existe un producto con SKU: " + sku);
            }
        });
    }

    private ProductoDTO.Response toResponseDTO(Producto p) {
        return ProductoDTO.Response.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .sku(p.getSku())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .stock(p.getStock())
                .estado(p.getEstado())
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .tieneStock(p.tieneStock())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private ProductoDTO.Summary toSummaryDTO(Producto p) {
        return ProductoDTO.Summary.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .sku(p.getSku())
                .precio(p.getPrecio())
                .stock(p.getStock())
                .estado(p.getEstado())
                .build();
    }
}
