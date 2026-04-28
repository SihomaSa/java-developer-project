package com.empresa.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de mensajería asíncrona con RabbitMQ
 * Integración entre microservicios / módulos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MensajeriaService {

    private final RabbitTemplate rabbitTemplate;

    public static final String EXCHANGE_PRODUCTOS = "productos.exchange";
    public static final String ROUTING_KEY_EVENTO = "productos.evento";
    public static final String ROUTING_KEY_STOCK = "productos.stock";

    /**
     * Publica un evento de producto al exchange de RabbitMQ
     * Se ejecuta en hilo separado (no bloquea la respuesta HTTP)
     */
    @Async
    public void publicarEventoProducto(String tipoEvento, Long productoId, String sku) {
        try {
            Map<String, Object> evento = new HashMap<>();
            evento.put("tipo", tipoEvento);
            evento.put("productoId", productoId);
            evento.put("sku", sku);
            evento.put("timestamp", Instant.now().toString());

            rabbitTemplate.convertAndSend(EXCHANGE_PRODUCTOS, ROUTING_KEY_EVENTO, evento);
            log.info("Evento '{}' publicado para producto id={}, sku={}", tipoEvento, productoId, sku);
        } catch (Exception e) {
            // No propagar - el mensaje no enviado no debe fallar la operación principal
            log.error("Error publicando evento '{}' para producto {}: {}", tipoEvento, productoId, e.getMessage());
        }
    }

    /**
     * Publica alerta de stock bajo
     */
    @Async
    public void publicarAlertaStockBajo(Long productoId, String sku, int stockActual) {
        try {
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("tipo", "STOCK_BAJO");
            alerta.put("productoId", productoId);
            alerta.put("sku", sku);
            alerta.put("stockActual", stockActual);
            alerta.put("timestamp", Instant.now().toString());

            rabbitTemplate.convertAndSend(EXCHANGE_PRODUCTOS, ROUTING_KEY_STOCK, alerta);
            log.warn("Alerta stock bajo publicada: producto={}, stock={}", sku, stockActual);
        } catch (Exception e) {
            log.error("Error publicando alerta stock: {}", e.getMessage());
        }
    }
}
