# 🚀 Java Developer Project — Full‑Stack Portfolio

> Proyecto Spring Boot completo que cubre **todas** las habilidades del desarrollador Java profesional, combinado con una **interfaz moderna** en React, TypeScript y Tailwind CSS.

---

## 📋 Tabla de Contenidos

- [Tecnologías](#-tecnologías)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Ejecución Rápida](#️-ejecución-rápida)
- [URLs Importantes](#-urls-importantes)
- [Credenciales de Prueba](#-credenciales-de-prueba)
- [Ejemplos de API](#-ejemplos-de-api-con-curl)
- [Desarrollo del Frontend](#️-desarrollo-del-frontend)
- [Ejecutar Tests](#-ejecutar-tests)
- [CI/CD Pipeline](#️-cicd-pipeline)
- [Patrones y Buenas Prácticas](#-patrones-y-buenas-prácticas)
- [Cambios Recientes](#-cambios-recientes)
- [Licencia](#-licencia)

---

## 🧰 Tecnologías

### Backend

| Categoría        | Tecnología                                      |
|------------------|-------------------------------------------------|
| **Framework**    | Spring Boot 3.2, Spring MVC                     |
| **Persistencia** | Spring Data JPA + Hibernate 6                   |
| **Validaciones** | Jakarta EE Bean Validation                      |
| **Arquitectura** | REST API, MVC, Microservicios (modular)          |
| **Mensajería**   | RabbitMQ (eventos asíncronos)                   |
| **Seguridad**    | Spring Security (roles, BCrypt)                 |
| **Testing**      | JUnit 5, Mockito, AssertJ, MockMvc              |
| **Cobertura**    | JaCoCo (80% mínimo)                             |
| **CI/CD**        | GitHub Actions (build → test → docker → deploy) |
| **Contenedores** | Docker multi-stage, Docker Compose              |
| **Documentación**| OpenAPI 3 / Swagger UI                          |
| **DevOps**       | Spring Actuator (health checks)                 |

### Frontend

| Categoría         | Tecnología                              |
|-------------------|-----------------------------------------|
| **Framework**     | React 18 + TypeScript                   |
| **Estilos**       | Tailwind CSS (modo oscuro incorporado)  |
| **Cliente HTTP**  | Axios con interceptores de autenticación|
| **Rutas**         | React Router DOM                        |
| **Notificaciones**| React Hot Toast                         |
| **Iconos**        | Lucide React                            |

---

## 📁 Estructura del Proyecto

```
java-developer-project/
├── src/main/java/com/empresa/app/
│   ├── controller/          # REST Controllers
│   ├── service/             # Lógica de negocio
│   ├── repository/          # Acceso a datos (JPA)
│   ├── model/               # Entidades JPA
│   ├── dto/                 # Data Transfer Objects
│   ├── exception/           # Manejo global de errores
│   └── config/              # Spring Security, RabbitMQ, Data Init
├── src/main/resources/
│   ├── static/              # Frontend construido (opcional)
│   └── application.properties
├── frontend/
│   └── src/
│       ├── components/      # Layout, etc.
│       ├── pages/           # Login, ProductList, ProductDetail, LowStock
│       ├── services/        # API client (Axios)
│       ├── context/         # AuthContext (Basic Auth)
│       └── types/           # TypeScript interfaces
├── docker-compose.yml
├── Dockerfile
└── README.md
```

---

## ▶️ Ejecución Rápida

### Opción 1 — Solo backend (local, H2 en memoria)

> Requiere Java 17+

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`. El frontend no está disponible en este modo.

---

### Opción 2 — Backend + Frontend con Docker ⭐ Recomendado

```bash
docker-compose up -d
```

| Servicio          | URL                                     |
|-------------------|-----------------------------------------|
| Backend API       | http://localhost:8080/api/v1            |
| Interfaz React    | http://localhost:8080                   |
| RabbitMQ          | http://localhost:15672 (guest/guest)    |

> El frontend se copia automáticamente dentro del contenedor en `src/main/resources/static` durante el build. Para desarrollo en caliente del frontend, consulta la sección [Desarrollo del Frontend](#️-desarrollo-del-frontend).

---

## 🔗 URLs Importantes

| URL                                    | Descripción                                      |
|----------------------------------------|--------------------------------------------------|
| http://localhost:8080                  | Interfaz React (productos, login, stock)         |
| http://localhost:8080/swagger-ui.html  | Swagger UI / Documentación de la API             |
| http://localhost:8080/actuator/health  | Health check                                     |
| http://localhost:15672                 | RabbitMQ Management (guest / guest)              |
| http://localhost:8080/h2-console       | Consola H2 (solo en perfil `default`)            |

---

## 🔑 Credenciales de Prueba

| Usuario      | Contraseña | Roles                           |
|--------------|------------|---------------------------------|
| `admin`      | `admin123` | ADMIN, PRODUCTOS, INVENTARIO    |
| `inventario` | `inv123`   | INVENTARIO                      |
| `usuario`    | `usr123`   | USER                            |

### Endpoints Públicos (sin autenticación)

- `GET /api/v1/productos`
- `GET /api/v1/productos/{id}`
- `GET /api/v1/productos/buscar`

### Endpoints Protegidos

| Acción                          | Roles requeridos         |
|---------------------------------|--------------------------|
| Actualizar stock                | ADMIN, INVENTARIO        |
| Ver productos con stock bajo    | ADMIN, INVENTARIO        |
| Crear / Modificar productos     | ADMIN, PRODUCTOS         |

---

## 🧪 Ejemplos de API (con curl)

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

### Listar productos

```bash
curl "http://localhost:8080/api/v1/productos?page=0&size=10"
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
  -d '{"operacion": "INCREMENTAR", "cantidad": 20}'
```

> **Nota:** El frontend calcula automáticamente la operación (`INCREMENTAR` o `REDUCIR`) según el cambio deseado.

---

## 🖥️ Desarrollo del Frontend

Para modificar la interfaz React sin reconstruir el backend en cada cambio:

**1. Inicia el backend** (Opción 1 o Docker).

**2. En otra terminal, levanta el servidor de desarrollo:**

```bash
cd frontend
npm install
npm run dev
```

**3. Abre** `http://localhost:5173`

> `vite.config.ts` ya incluye un proxy que redirige `/api` al backend, evitando problemas de CORS.

---

### Integrar el frontend en el backend (producción)

```bash
cd frontend
npm run build
cp -r dist/* ../src/main/resources/static/
cd ..
docker-compose build --no-cache
docker-compose up -d
```

---

## 🧪 Ejecutar Tests

```bash
# Todos los tests
./mvnw test

# Con reporte de cobertura JaCoCo
./mvnw test jacoco:report
```

📄 El reporte HTML se genera en: `target/site/jacoco/index.html`

---

## 🏗️ CI/CD Pipeline

El pipeline (`.github/workflows/ci-cd.yml`) ejecuta los siguientes pasos automáticamente:

```
Build  →  Test (JaCoCo ≥80%)  →  Docker Build  →  Deploy (DockerHub + SSH)
```

---

## 📚 Patrones y Buenas Prácticas

### Backend

- ✅ Separación de capas: `Controller → Service → Repository`
- ✅ DTOs para no exponer entidades directamente
- ✅ Manejo global de errores (RFC 7807 Problem Details)
- ✅ Transacciones declarativas con `@Transactional`
- ✅ Consultas optimizadas (`JOIN FETCH`) para evitar el problema N+1
- ✅ Eventos asíncronos via RabbitMQ
- ✅ Tests unitarios con Mockito (estilo BDD)
- ✅ Tests de integración con MockMvc
- ✅ Seguridad por roles con Spring Security y `@PreAuthorize`
- ✅ Paginación con Spring Data `Pageable`
- ✅ Logging estructurado con SLF4J/Logback
- ✅ Documentación automática con OpenAPI 3

### Frontend

- ✅ Componentes funcionales con Hooks
- ✅ Tipado fuerte con TypeScript
- ✅ Autenticación persistente (`localStorage`) + interceptores de Axios
- ✅ Manejo de sesión mediante Context API
- ✅ Modo oscuro configurable y persistente
- ✅ Diseño responsivo con Tailwind CSS
- ✅ Notificaciones (toast) para acciones del usuario
- ✅ Separación de servicios (`apiClient`, `productService`)

---

## 🔧 Cambios Recientes

- ✨ **Frontend integrado en Docker** — Los archivos estáticos de React se sirven directamente desde `/static`.
- ✨ **DTO `StockRequest` corregido** — Ahora acepta `operacion` y `cantidad` para incrementar/reducir stock desde el frontend.
- ✨ **Interceptor de Axios ajustado** — No envía cabeceras `Authorization` en peticiones GET públicas (evita errores 401).
- ✨ **Contraseña en `localStorage`** — Permite reutilizar la autenticación en todas las peticiones protegidas.
- ✨ **Healthcheck de Docker** — Migrado de `wget` a `curl` para compatibilidad con imágenes Alpine JRE.
- ✨ **Variables de entorno en `docker-compose.yml`** — Configuración sensible separada en archivo `.env` (ignorado por git).

> ⚠️ Recuerda eliminar la línea `version: '3.8'` de tu `docker-compose.yml` para evitar el warning de obsolescencia. Asegúrate también de que el archivo `.env` con credenciales reales **no se suba** al repositorio (agrégalo a `.gitignore`).

---

## 📝 Licencia

Este proyecto es de **uso educativo**. Puedes forkearlo, mejorarlo y adaptarlo libremente.

Si encuentras algún error o deseas proponer una mejora, por favor abre un **issue** o un **pull request**. ¡Las contribuciones son bienvenidas! 🙌

---

> 💡 **¿Qué habilidades cubre este proyecto?**
> Desde Spring Boot e Hibernate, pasando por Docker, CI/CD, APIs REST y mensajería asíncrona, hasta testing completo y un frontend profesional en React. Es el **portfolio definitivo** para un desarrollador Java full‑stack.