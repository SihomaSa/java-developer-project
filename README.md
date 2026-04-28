# 🚀 Java Developer Project

Proyecto Spring Boot completo que cubre **todas** las habilidades del desarrollador Java profesional.

---

## 🧰 Tecnologías Incluidas

| Categoría | Tecnología |
|-----------|-----------|
| **Framework** | Spring Boot 3.2, Spring MVC |
| **Persistencia** | Spring Data JPA + Hibernate 6 |
| **Validaciones** | Jakarta EE Bean Validation |
| **Arquitectura** | REST API, MVC, Microservicios (modular) |
| **Mensajería** | RabbitMQ (eventos asíncronos) |
| **Seguridad** | Spring Security (roles, BCrypt) |
| **Testing** | JUnit 5, Mockito, AssertJ, MockMvc |
| **Cobertura** | JaCoCo (80% mínimo) |
| **CI/CD** | GitHub Actions (build → test → docker → deploy) |
| **Contenedores** | Docker multi-stage, Docker Compose |
| **Documentación** | OpenAPI 3 / Swagger UI |
| **DevOps** | Spring Actuator (health checks) |

---

## 📁 Estructura del Proyecto

```
src/
├── main/java/com/empresa/app/
│   ├── controller/        # REST Controllers (MVC)
│   ├── service/           # Lógica de negocio
│   ├── repository/        # Acceso a datos (JPA)
│   ├── model/             # Entidades JPA (Hibernate)
│   ├── dto/               # Data Transfer Objects
│   ├── exception/         # Manejo global de errores
│   └── config/            # Spring Security, RabbitMQ, Data Init
└── test/java/com/empresa/app/
    ├── controller/        # Tests de integración (MockMvc)
    └── service/           # Tests unitarios (Mockito)
```

---

## ▶️ Ejecución Rápida

### Opción 1: Maven (local, H2 en memoria)
```bash
# Requiere Java 17+
./mvnw spring-boot:run
```
Acceder en: http://localhost:8080

### Opción 2: Docker Compose (PostgreSQL + RabbitMQ)
```bash
docker-compose up -d
```

---

## 🔗 URLs Importantes

| URL | Descripción |
|-----|-------------|
| http://localhost:8080/swagger-ui.html | Swagger UI / API Docs |
| http://localhost:8080/h2-console | Consola H2 (solo dev) |
| http://localhost:8080/actuator/health | Health check |
| http://localhost:15672 | RabbitMQ Management (guest/guest) |

---

## 🧪 Ejecutar Tests

```bash
# Todos los tests
./mvnw test

# Con reporte de cobertura JaCoCo
./mvnw test jacoco:report

# Reporte en: target/site/jacoco/index.html
```

---

## 🔑 Credenciales de Prueba

| Usuario | Contraseña | Roles |
|---------|-----------|-------|
| admin | admin123 | ADMIN, PRODUCTOS, INVENTARIO |
| inventario | inv123 | INVENTARIO |
| usuario | usr123 | USER |

---

## 📡 Ejemplos de API

### Crear producto
```bash
curl -X POST http://localhost:8080/api/v1/productos \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laptop Pro",
    "sku": "LAP-PRO-001",
    "precio": 1299.99,
    "stock": 50
  }'
```

### Listar productos (paginado)
```bash
curl "http://localhost:8080/api/v1/productos?page=0&size=10&sort=nombre,asc"
```

### Buscar por texto
```bash
curl "http://localhost:8080/api/v1/productos/buscar?q=laptop"
```

### Actualizar stock
```bash
curl -X PUT http://localhost:8080/api/v1/productos/1/stock \
  -u inventario:inv123 \
  -H "Content-Type: application/json" \
  -d '{"cantidad": 20, "operacion": "INCREMENTAR"}'
```

---

## 🏗️ CI/CD Pipeline

El pipeline (`.github/workflows/ci-cd.yml`) ejecuta:

1. **Build** - Compila el proyecto
2. **Test** - JUnit 5 + JaCoCo cobertura ≥80%
3. **Docker Build** - Imagen multi-stage optimizada
4. **Deploy** - Push a DockerHub + deploy por SSH (rama `main`)

---

## 📚 Patrones y Buenas Prácticas

- ✅ **Separación de capas**: Controller → Service → Repository
- ✅ **DTOs** para no exponer entidades directamente
- ✅ **Manejo global de errores** (RFC 7807 Problem Details)
- ✅ **Transacciones** declarativas con `@Transactional`
- ✅ **Consultas optimizadas** (JOIN FETCH para evitar N+1)
- ✅ **Eventos asíncronos** via RabbitMQ
- ✅ **Tests unitarios** con Mockito (BDD style)
- ✅ **Tests de integración** con MockMvc
- ✅ **Seguridad por roles** con Spring Security
- ✅ **Paginación** con Spring Data Pageable
- ✅ **Logging** estructurado con SLF4J/Logback
- ✅ **Documentación** automática con OpenAPI 3
