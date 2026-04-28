package com.empresa.app.repository;

import com.empresa.app.model.Producto;
import com.empresa.app.model.Producto.EstadoProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para Producto
 * - Spring Data JPA (queries automáticas)
 * - JPQL y queries nativas
 * - Paginación y ordenación
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Query derivada del nombre del método
    Optional<Producto> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Producto> findByEstado(EstadoProducto estado);

    // Paginación
    Page<Producto> findByEstado(EstadoProducto estado, Pageable pageable);

    Page<Producto> findByCategoriaId(Long categoriaId, Pageable pageable);

    // JPQL con JOIN FETCH para evitar N+1
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria WHERE p.id = :id")
    Optional<Producto> findByIdWithCategoria(@Param("id") Long id);

    // Búsqueda por texto (nombre o descripción)
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Producto> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    // Filtrado por rango de precios
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :min AND :max AND p.estado = 'ACTIVO'")
    List<Producto> findByPrecioRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    // Productos con stock bajo
    @Query("SELECT p FROM Producto p WHERE p.stock <= :umbral AND p.estado = 'ACTIVO' ORDER BY p.stock ASC")
    List<Producto> findProductosConStockBajo(@Param("umbral") int umbral);

    // Estadísticas
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.estado = :estado")
    long countByEstado(@Param("estado") EstadoProducto estado);

    @Query("SELECT AVG(p.precio) FROM Producto p WHERE p.categoria.id = :categoriaId AND p.estado = 'ACTIVO'")
    Optional<BigDecimal> findPrecioPromedioPorCategoria(@Param("categoriaId") Long categoriaId);

    // Update masivo (nativo)
    @Modifying
    @Query("UPDATE Producto p SET p.estado = 'AGOTADO' WHERE p.stock = 0 AND p.estado = 'ACTIVO'")
    int marcarAgotados();

    // Query nativa para reportes complejos
    @Query(value = """
            SELECT c.nombre as categoria, COUNT(p.id) as total, SUM(p.stock) as stock_total,
                   AVG(p.precio) as precio_promedio
            FROM productos p
            INNER JOIN categorias c ON p.categoria_id = c.id
            WHERE p.estado = 'ACTIVO'
            GROUP BY c.id, c.nombre
            ORDER BY total DESC
            """, nativeQuery = true)
    List<Object[]> getResumenPorCategoria();
}
