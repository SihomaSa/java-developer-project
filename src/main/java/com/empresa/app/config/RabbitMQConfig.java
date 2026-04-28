package com.empresa.app.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.empresa.app.service.MensajeriaService.*;

/**
 * Configuración de RabbitMQ
 * - Exchange topic para ruteo flexible entre microservicios
 * - Colas con Dead Letter Queue (DLQ) para manejo de errores
 * - Serialización JSON para los mensajes
 */
@Configuration
public class RabbitMQConfig {

    // === COLAS ===
    @Bean
    public Queue colaEventosProductos() {
        return QueueBuilder.durable("productos.eventos.queue")
                .withArgument("x-dead-letter-exchange", "productos.dlx")
                .withArgument("x-dead-letter-routing-key", "productos.dead")
                .withArgument("x-message-ttl", 86400000) // 24h TTL
                .build();
    }

    @Bean
    public Queue colaStockBajo() {
        return QueueBuilder.durable("productos.stock.queue")
                .withArgument("x-dead-letter-exchange", "productos.dlx")
                .build();
    }

    @Bean
    public Queue colaDeadLetter() {
        return QueueBuilder.durable("productos.dead.queue").build();
    }

    // === EXCHANGES ===
    @Bean
    public TopicExchange exchangeProductos() {
        return ExchangeBuilder.topicExchange(EXCHANGE_PRODUCTOS).durable(true).build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange("productos.dlx").durable(true).build();
    }

    // === BINDINGS ===
    @Bean
    public Binding bindingEventos(Queue colaEventosProductos, TopicExchange exchangeProductos) {
        return BindingBuilder.bind(colaEventosProductos)
                .to(exchangeProductos)
                .with(ROUTING_KEY_EVENTO);
    }

    @Bean
    public Binding bindingStock(Queue colaStockBajo, TopicExchange exchangeProductos) {
        return BindingBuilder.bind(colaStockBajo)
                .to(exchangeProductos)
                .with(ROUTING_KEY_STOCK);
    }

    @Bean
    public Binding bindingDeadLetter(Queue colaDeadLetter, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(colaDeadLetter)
                .to(deadLetterExchange)
                .with("productos.dead");
    }

    // === CONFIGURACIÓN ===
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setMandatory(true);
        return template;
    }
}
