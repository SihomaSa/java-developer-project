package com.empresa.app.config;

import com.empresa.app.model.Categoria;
import com.empresa.app.model.Producto;
import com.empresa.app.model.Producto.EstadoProducto;
import com.empresa.app.repository.CategoriaRepository;
import com.empresa.app.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

/**
 * Datos iniciales para pruebas/desarrollo
 * Solo activo en perfil "dev" (no en producción ni tests)
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    @Profile("!prod")
    public CommandLineRunner initData(CategoriaRepository categoriaRepo,
                                      ProductoRepository productoRepo) {
        return args -> {
            log.info("Inicializando datos de prueba...");

            // Categorías
            Categoria electronica = categoriaRepo.save(
                    Categoria.builder().nombre("Electrónica").descripcion("Dispositivos electrónicos").build());
            Categoria ropa = categoriaRepo.save(
                    Categoria.builder().nombre("Ropa").descripcion("Prendas de vestir").build());
            Categoria alimentos = categoriaRepo.save(
                    Categoria.builder().nombre("Alimentos").descripcion("Productos alimenticios").build());

            // Productos
            productoRepo.save(Producto.builder()
                    .nombre("Laptop Pro 15")
                    .sku("LAP-PRO-15")
                    .descripcion("Laptop profesional de alto rendimiento con procesador Intel i7")
                    .precio(new BigDecimal("2499.99"))
                    .stock(50)
                    .estado(EstadoProducto.ACTIVO)
                    .categoria(electronica)
                    .build());

            productoRepo.save(Producto.builder()
                    .nombre("Smartphone Galaxy X")
                    .sku("SMT-GAL-X")
                    .descripcion("Smartphone con cámara de 108MP y batería de 5000mAh")
                    .precio(new BigDecimal("899.99"))
                    .stock(120)
                    .estado(EstadoProducto.ACTIVO)
                    .categoria(electronica)
                    .build());

            productoRepo.save(Producto.builder()
                    .nombre("Audífonos Bluetooth")
                    .sku("AUD-BT-001")
                    .descripcion("Audífonos inalámbricos con cancelación de ruido activa")
                    .precio(new BigDecimal("149.99"))
                    .stock(3) // Stock bajo para demostrar alertas
                    .estado(EstadoProducto.ACTIVO)
                    .categoria(electronica)
                    .build());

            productoRepo.save(Producto.builder()
                    .nombre("Camiseta Polo Clásica")
                    .sku("CAM-POL-CL")
                    .descripcion("Camiseta polo 100% algodón en varios colores")
                    .precio(new BigDecimal("39.99"))
                    .stock(200)
                    .estado(EstadoProducto.ACTIVO)
                    .categoria(ropa)
                    .build());

            productoRepo.save(Producto.builder()
                    .nombre("Café Premium Orgánico")
                    .sku("CAF-PRE-ORG")
                    .descripcion("Café de altura, tostado medio, 500g")
                    .precio(new BigDecimal("24.99"))
                    .stock(0) // Sin stock para mostrar estado AGOTADO
                    .estado(EstadoProducto.AGOTADO)
                    .categoria(alimentos)
                    .build());

            log.info("Datos de prueba inicializados: {} categorías, {} productos",
                    categoriaRepo.count(), productoRepo.count());
        };
    }
}
