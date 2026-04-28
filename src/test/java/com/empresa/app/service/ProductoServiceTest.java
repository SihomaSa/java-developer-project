package com.empresa.app.service;

import com.empresa.app.dto.ProductoDTO;
import com.empresa.app.exception.RecursoNoEncontradoException;
import com.empresa.app.exception.ReglaDeNegocioException;
import com.empresa.app.model.Categoria;
import com.empresa.app.model.Producto;
import com.empresa.app.model.Producto.EstadoProducto;
import com.empresa.app.repository.CategoriaRepository;
import com.empresa.app.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Tests unitarios para ProductoService
 * - JUnit 5 con @Nested para organización
 * - Mockito para mocking de dependencias
 * - AssertJ para assertions fluidas
 * - BDD Style (given/when/then)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService - Tests Unitarios")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private MensajeriaService mensajeriaService;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoMock;
    private ProductoDTO.Request requestMock;

    @BeforeEach
    void setUp() {
        productoMock = Producto.builder()
                .id(1L)
                .nombre("Laptop Test")
                .sku("LAP-TEST-01")
                .precio(new BigDecimal("999.99"))
                .stock(10)
                .estado(EstadoProducto.ACTIVO)
                .build();

        requestMock = ProductoDTO.Request.builder()
                .nombre("Laptop Test")
                .sku("LAP-TEST-01")
                .precio(new BigDecimal("999.99"))
                .stock(10)
                .build();
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("Debe retornar producto cuando existe")
        void debeRetornarProductoCuandoExiste() {
            // Given
            given(productoRepository.findByIdWithCategoria(1L))
                    .willReturn(Optional.of(productoMock));

            // When
            ProductoDTO.Response resultado = productoService.obtenerPorId(1L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNombre()).isEqualTo("Laptop Test");
            assertThat(resultado.getSku()).isEqualTo("LAP-TEST-01");
            verify(productoRepository, times(1)).findByIdWithCategoria(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando no existe")
        void debeLanzarExcepcionCuandoNoExiste() {
            // Given
            given(productoRepository.findByIdWithCategoria(99L))
                    .willReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear producto exitosamente")
        void debeCrearProductoExitosamente() {
            // Given
            given(productoRepository.findBySku("LAP-TEST-01")).willReturn(Optional.empty());
            given(productoRepository.save(any(Producto.class))).willReturn(productoMock);

            // When
            ProductoDTO.Response resultado = productoService.crear(requestMock);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getSku()).isEqualTo("LAP-TEST-01");

            // Verificar que se guardó el producto
            ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
            verify(productoRepository).save(captor.capture());
            assertThat(captor.getValue().getSku()).isEqualTo("LAP-TEST-01");
            assertThat(captor.getValue().getEstado()).isEqualTo(EstadoProducto.ACTIVO);

            // Verificar evento publicado
            verify(mensajeriaService).publicarEventoProducto(eq("PRODUCTO_CREADO"), any(), eq("LAP-TEST-01"));
        }

        @Test
        @DisplayName("Debe fallar si SKU ya existe")
        void debeFallarSiSkuYaExiste() {
            // Given
            given(productoRepository.findBySku("LAP-TEST-01"))
                    .willReturn(Optional.of(productoMock));

            // When / Then
            assertThatThrownBy(() -> productoService.crear(requestMock))
                    .isInstanceOf(ReglaDeNegocioException.class)
                    .hasMessageContaining("LAP-TEST-01");

            verify(productoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe asignar categoría cuando se proporciona categoriaId")
        void debeAsignarCategoriaConCategoriaId() {
            // Given
            Categoria categoria = Categoria.builder().id(5L).nombre("Electrónica").build();
            requestMock.setCategoriaId(5L);

            given(productoRepository.findBySku(anyString())).willReturn(Optional.empty());
            given(categoriaRepository.findById(5L)).willReturn(Optional.of(categoria));
            given(productoRepository.save(any())).willReturn(productoMock);

            // When
            productoService.crear(requestMock);

            // Then
            ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
            verify(productoRepository).save(captor.capture());
            assertThat(captor.getValue().getCategoria()).isEqualTo(categoria);
        }
    }

    @Nested
    @DisplayName("actualizarStock()")
    class ActualizarStock {

        @Test
        @DisplayName("Debe incrementar stock correctamente")
        void debeIncrementarStock() {
            // Given
            given(productoRepository.findById(1L)).willReturn(Optional.of(productoMock));
            given(productoRepository.save(any())).willReturn(productoMock);

            var stockReq = new ProductoDTO.StockRequest(5, "INCREMENTAR");

            // When
            productoService.actualizarStock(1L, stockReq);

            // Then
            assertThat(productoMock.getStock()).isEqualTo(15);
        }

        @Test
        @DisplayName("Debe marcar como AGOTADO cuando stock llega a 0")
        void debeMarcarAgotadoCuandoStockCeroAlReducir() {
            // Given
            productoMock.setStock(5);
            given(productoRepository.findById(1L)).willReturn(Optional.of(productoMock));
            given(productoRepository.save(any())).willReturn(productoMock);

            var stockReq = new ProductoDTO.StockRequest(5, "REDUCIR");

            // When
            productoService.actualizarStock(1L, stockReq);

            // Then
            assertThat(productoMock.getStock()).isEqualTo(0);
            assertThat(productoMock.getEstado()).isEqualTo(EstadoProducto.AGOTADO);
            verify(mensajeriaService).publicarEventoProducto(eq("PRODUCTO_AGOTADO"), any(), any());
        }

        @Test
        @DisplayName("Debe fallar con operación inválida")
        void debeFallarConOperacionInvalida() {
            // Given
            given(productoRepository.findById(1L)).willReturn(Optional.of(productoMock));
            var stockReq = new ProductoDTO.StockRequest(5, "OPERACION_INVALIDA");

            // When / Then
            assertThatThrownBy(() -> productoService.actualizarStock(1L, stockReq))
                    .isInstanceOf(ReglaDeNegocioException.class);
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar producto existente")
        void debeEliminarProductoExistente() {
            // Given
            given(productoRepository.existsById(1L)).willReturn(true);

            // When
            productoService.eliminar(1L);

            // Then
            verify(productoRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción si no existe")
        void debeLanzarExcepcionSiNoExiste() {
            // Given
            given(productoRepository.existsById(99L)).willReturn(false);

            // When / Then
            assertThatThrownBy(() -> productoService.eliminar(99L))
                    .isInstanceOf(RecursoNoEncontradoException.class);

            verify(productoRepository, never()).deleteById(any());
        }
    }
}
