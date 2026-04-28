package com.empresa.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Aplicación principal - Spring Boot
 * Proyecto Java Desarrollador Completo
 *
 * Tecnologías cubiertas:
 * - Spring Boot (Framework principal)
 * - Hibernate/JPA (Persistencia)
 * - Jakarta EE (Validaciones)
 * - REST APIs + MVC
 * - Microservicios (arquitectura modular)
 * - RabbitMQ (Mensajería asíncrona)
 * - JUnit 5 + Mockito (Testing)
 * - CI/CD ready (Actuator + JaCoCo)
 */
@SpringBootApplication
@EnableAsync
public class JavaDeveloperApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaDeveloperApplication.class, args);
    }
}
