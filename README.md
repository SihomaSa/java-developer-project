# 🚀 Java Developer Project – Proyecto Full‑Stack

Proyecto Spring Boot completo que cubre **todas** las habilidades del desarrollador Java profesional, incluyendo una **interfaz de usuario moderna** construida con React, TypeScript y Tailwind CSS.

---

## 🧰 Tecnologías Incluidas

### Backend
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

### Frontend
| Categoría | Tecnología |
|-----------|-----------|
| **Framework** | React 18 + TypeScript |
| **Estilos** | Tailwind CSS (modo oscuro incorporado) |
| **Cliente HTTP** | Axios con interceptores de autenticación |
| **Rutas** | React Router DOM |
| **Notificaciones** | React Hot Toast |
| **Iconos** | Lucide React |

---

## 📁 Estructura del Proyecto

java-developer-project/ # Raíz del backend
├── src/main/java/com/empresa/app/ # Backend Spring Boot
│ ├── controller/ # REST Controllers
│ ├── service/ # Lógica de negocio
│ ├── repository/ # Acceso a datos (JPA)
│ ├── model/ # Entidades JPA
│ ├── dto/ # Data Transfer Objects
│ ├── exception/ # Manejo global de errores
│ └── config/ # Spring Security, RabbitMQ, Data Init
├── src/main/resources/
│ ├── static/ # Frontend construido (opcional)
│ └── application.properties
├── frontend/ # Frontend React + TypeScript
│ ├── src/
│ │ ├── components/ # Layout, etc.
│ │ ├── pages/ # Login, ProductList, ProductDetail, LowStock
│ │ ├── services/ # API client (Axios)
│ │ ├── context/ # AuthContext (Basic Auth)
│ │ └── types/ # TypeScript interfaces
│ ├── package.json
│ └── vite.config.ts
├── docker-compose.yml
├── Dockerfile
└── README.md
text


---

## ▶️ Ejecución Rápida

### Opción 1: Solo backend (local, H2 en memoria)
```bash
# Requiere Java 17+
./mvnw spring-boot:run

Acceder a la API en: http://localhost:8080
Frontend no disponible (solo API en este modo).
Opción 2: Backend + Frontend con Docker (recomendado)
bash

docker-compose up -d

    Backend API → http://localhost:8080/api/v1

    Interfaz React → http://localhost:8080 (integrada en /static)

    RabbitMQ Management → http://localhost:15672 (guest/guest)

    El frontend se copia automáticamente dentro del contenedor en src/main/resources/static durante el build. Si deseas desarrollar el frontend por separado, consulta la sección Desarrollo del Frontend.

🔗 URLs Importantes
URL	Descripción
http://localhost:8080	Interfaz de usuario React (productos, login, gestión de stock)
http://localhost:8080/swagger-ui.html	Swagger UI / API Docs
http://localhost:8080/actuator/health	Health check
http://localhost:15672	RabbitMQ Management (guest/guest)
http://localhost:8080/h2-console	Consola H2 (solo si se usa perfil default)
🔑 Credenciales de Prueba (frontend + backend)

La interfaz utiliza autenticación Basic Auth con los siguientes usuarios:
Usuario	Contraseña	Roles
admin	admin123	ADMIN, PRODUCTOS, INVENTARIO
inventario	inv123	INVENTARIO
usuario	usr123	USER

Endpoints públicos (acceso sin login):

    GET /api/v1/productos

    GET /api/v1/productos/{id}

    GET /api/v1/productos/buscar

Opciones protegidas (requieren login):

    Actualizar stock (solo ADMIN e INVENTARIO)

    Ver productos con stock bajo (solo ADMIN e INVENTARIO)

    Crear/Modificar productos (ADMIN o PRODUCTOS)

🧪 Ejemplos de API (con curl)
Crear producto (requiere ADMIN o PRODUCTOS)
bash

curl -X POST http://localhost:8080/api/v1/productos \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laptop Pro",
    "sku": "LAP-PRO-001",
    "precio": 1299.99,
    "stock": 50
  }'

Listar productos (público)
bash

curl "http://localhost:8080/api/v1/productos?page=0&size=10"

Buscar por texto (público)
bash

curl "http://localhost:8080/api/v1/productos/buscar?q=laptop"

Actualizar stock (requiere INVENTARIO)
bash

curl -X PUT http://localhost:8080/api/v1/productos/1/stock \
  -u inventario:inv123 \
  -H "Content-Type: application/json" \
  -d '{"operacion": "INCREMENTAR", "cantidad": 20}'

    Nota: El frontend, al actualizar stock, calcula automáticamente la operación (INCREMENTAR o REDUCIR) en función del cambio deseado.

🖥️ Desarrollo del Frontend (local, con recarga en caliente)

Si deseas modificar la interfaz React sin reconstruir el backend cada vez:

    Inicia el backend (opción 1 o Docker).

    En otra terminal, ve a la carpeta frontend:
    bash

    cd frontend
    npm install
    npm run dev

    Abre http://localhost:5173 (Vite) – el proxy está configurado para redirigir /api al backend.

    El archivo vite.config.ts ya incluye un proxy para evitar problemas de CORS durante el desarrollo.

Para construir el frontend e integrarlo dentro del backend:
bash

cd frontend
npm run build
cp -r dist/* ../src/main/resources/static/
cd ..
docker-compose build --no-cache
docker-compose up -d

🧪 Ejecutar Tests (backend)
bash

# Todos los tests
./mvnw test

# Con reporte de cobertura JaCoCo
./mvnw test jacoco:report

# Reporte en: target/site/jacoco/index.html

🏗️ CI/CD Pipeline (GitHub Actions)

El pipeline (.github/workflows/ci-cd.yml) ejecuta:

    Build – Compila el backend y el frontend.

    Test – JUnit 5 + JaCoCo cobertura ≥80%.

    Docker Build – Construye imagen multi‑stage que incluye el frontend.

    Deploy – Push a DockerHub y despliegue por SSH (rama main).

📚 Patrones y Buenas Prácticas Aplicados
Backend

    ✅ Separación de capas: Controller → Service → Repository

    ✅ DTOs para no exponer entidades directamente

    ✅ Manejo global de errores (RFC 7807 Problem Details)

    ✅ Transacciones declarativas con @Transactional

    ✅ Consultas optimizadas (JOIN FETCH) para evitar problema N+1

    ✅ Eventos asíncronos via RabbitMQ

    ✅ Tests unitarios con Mockito (estilo BDD)

    ✅ Tests de integración con MockMvc

    ✅ Seguridad por roles con Spring Security y @PreAuthorize

    ✅ Paginación con Spring Data Pageable

    ✅ Logging estructurado con SLF4J/Logback

    ✅ Documentación automática con OpenAPI 3

Frontend

    ✅ Componentes funcionales con Hooks

    ✅ Tipado fuerte con TypeScript

    ✅ Autenticación persistente (localStorage) + interceptores de Axios

    ✅ Manejo de sesión mediante Context API

    ✅ Modo oscuro configurable y persistente

    ✅ Diseño responsivo con Tailwind CSS

    ✅ Mensajes de notificación (toast) para acciones del usuario

    ✅ Separación de servicios (api cliente, productService)

🔧 Últimas Mejoras / Cambios Recientes

    ✨ Integración del frontend React dentro del mismo contenedor Docker (archivos estáticos en /static).

    ✨ Corrección del DTO StockRequest – Ahora acepta operacion y cantidad para que el frontend pueda incrementar/reducir stock correctamente.

    ✨ Ajuste en el interceptor de Axios – No envía cabeceras Authorization en peticiones GET públicas (evita errores 401).

    ✨ Guardado de contraseña en localStorage – Permite reutilizar la autenticación en todas las peticiones protegidas.

    ✨ Healthcheck de Docker modificado de wget a curl para imágenes basadas en Alpine (compatible con JRE).

    ✨ Variables de entorno en docker-compose.yml para separar configuración sensible (se recomienda usar archivo .env ignorado por git).

📝 Licencia y Contribuciones

Este proyecto es de uso educativo. Puedes forkearlo, mejorarlo y adaptarlo libremente.

Si encuentras algún error o deseas proponer una mejora, por favor abre un issue o un pull request. ¡Las contribuciones son bienvenidas!

¿Qué habilidades cubre este proyecto?
Desde Spring Boot y Hibernate, pasando por Docker, CI/CD, APIs REST, mensajería asíncrona, testing unitario/de integración, hasta un frontend profesional en React. Es el portfolio definitivo para un desarrollador Java full‑stack.
text


> **Nota**: Recuerda que debes eliminar la línea `version: '3.8'` de tu `docker-compose.yml` para evitar el warning. También asegúrate de que el archivo `.env` con credenciales reales **no se suba** al repositorio (inclúyelo en `.gitignore`). El README ya refleja esta buena práctica.

Si necesitas ajustar algún detalle más (como agregar capturas de pantalla o enlaces a despliegue en vivo), dímelo.

m epuedes dar el readme para decargar

We need to provide a README.md file content that the user can download. The user asked: "me puedes dar el readme para descargar". So I will output the full README.md content as a code block, so they can copy and save as README.md file. I'll include the latest frontend additions and improvements.

Claro, aquí tienes el contenido completo del README.md para que puedas copiarlo y guardarlo en la raíz de tu proyecto. Solo tienes que crear un archivo llamado README.md y pegar este contenido.
markdown

# 🚀 Java Developer Project – Proyecto Full‑Stack

Proyecto Spring Boot completo que cubre **todas** las habilidades del desarrollador Java profesional, incluyendo una **interfaz de usuario moderna** construida con React, TypeScript y Tailwind CSS.

---

## 🧰 Tecnologías Incluidas

### Backend
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

### Frontend
| Categoría | Tecnología |
|-----------|-----------|
| **Framework** | React 18 + TypeScript |
| **Estilos** | Tailwind CSS (modo oscuro incorporado) |
| **Cliente HTTP** | Axios con interceptores de autenticación |
| **Rutas** | React Router DOM |
| **Notificaciones** | React Hot Toast |
| **Iconos** | Lucide React |

---

## 📁 Estructura del Proyecto

java-developer-project/ # Raíz del backend
├── src/main/java/com/empresa/app/ # Backend Spring Boot
│ ├── controller/ # REST Controllers
│ ├── service/ # Lógica de negocio
│ ├── repository/ # Acceso a datos (JPA)
│ ├── model/ # Entidades JPA
│ ├── dto/ # Data Transfer Objects
│ ├── exception/ # Manejo global de errores
│ └── config/ # Spring Security, RabbitMQ, Data Init
├── src/main/resources/
│ ├── static/ # Frontend construido (opcional)
│ └── application.properties
├── frontend/ # Frontend React + TypeScript
│ ├── src/
│ │ ├── components/ # Layout, etc.
│ │ ├── pages/ # Login, ProductList, ProductDetail, LowStock
│ │ ├── services/ # API client (Axios)
│ │ ├── context/ # AuthContext (Basic Auth)
│ │ └── types/ # TypeScript interfaces
│ ├── package.json
│ └── vite.config.ts
├── docker-compose.yml
├── Dockerfile
└── README.md
text


---

## ▶️ Ejecución Rápida

### Opción 1: Solo backend (local, H2 en memoria)
```bash
# Requiere Java 17+
./mvnw spring-boot:run

Acceder a la API en: http://localhost:8080
Frontend no disponible (solo API en este modo).
Opción 2: Backend + Frontend con Docker (recomendado)
bash

docker-compose up -d

    Backend API → http://localhost:8080/api/v1

    Interfaz React → http://localhost:8080 (integrada en /static)

    RabbitMQ Management → http://localhost:15672 (guest/guest)

    El frontend se copia automáticamente dentro del contenedor en src/main/resources/static durante el build. Si deseas desarrollar el frontend por separado, consulta la sección Desarrollo del Frontend.

🔗 URLs Importantes
URL	Descripción
http://localhost:8080	Interfaz de usuario React (productos, login, gestión de stock)
http://localhost:8080/swagger-ui.html	Swagger UI / API Docs
http://localhost:8080/actuator/health	Health check
http://localhost:15672	RabbitMQ Management (guest/guest)
http://localhost:8080/h2-console	Consola H2 (solo si se usa perfil default)
🔑 Credenciales de Prueba (frontend + backend)

La interfaz utiliza autenticación Basic Auth con los siguientes usuarios:
Usuario	Contraseña	Roles
admin	admin123	ADMIN, PRODUCTOS, INVENTARIO
inventario	inv123	INVENTARIO
usuario	usr123	USER

Endpoints públicos (acceso sin login):

    GET /api/v1/productos

    GET /api/v1/productos/{id}

    GET /api/v1/productos/buscar

Opciones protegidas (requieren login):

    Actualizar stock (solo ADMIN e INVENTARIO)

    Ver productos con stock bajo (solo ADMIN e INVENTARIO)

    Crear/Modificar productos (ADMIN o PRODUCTOS)

🧪 Ejemplos de API (con curl)
Crear producto (requiere ADMIN o PRODUCTOS)
bash

curl -X POST http://localhost:8080/api/v1/productos \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laptop Pro",
    "sku": "LAP-PRO-001",
    "precio": 1299.99,
    "stock": 50
  }'

Listar productos (público)
bash

curl "http://localhost:8080/api/v1/productos?page=0&size=10"

Buscar por texto (público)
bash

curl "http://localhost:8080/api/v1/productos/buscar?q=laptop"

Actualizar stock (requiere INVENTARIO)
bash

curl -X PUT http://localhost:8080/api/v1/productos/1/stock \
  -u inventario:inv123 \
  -H "Content-Type: application/json" \
  -d '{"operacion": "INCREMENTAR", "cantidad": 20}'

    Nota: El frontend, al actualizar stock, calcula automáticamente la operación (INCREMENTAR o REDUCIR) en función del cambio deseado.

🖥️ Desarrollo del Frontend (local, con recarga en caliente)

Si deseas modificar la interfaz React sin reconstruir el backend cada vez:

    Inicia el backend (opción 1 o Docker).

    En otra terminal, ve a la carpeta frontend:
    bash

    cd frontend
    npm install
    npm run dev

    Abre http://localhost:5173 (Vite) – el proxy está configurado para redirigir /api al backend.

    El archivo vite.config.ts ya incluye un proxy para evitar problemas de CORS durante el desarrollo.

Para construir el frontend e integrarlo dentro del backend:
bash

cd frontend
npm run build
cp -r dist/* ../src/main/resources/static/
cd ..
docker-compose build --no-cache
docker-compose up -d

🧪 Ejecutar Tests (backend)
bash

# Todos los tests
./mvnw test

# Con reporte de cobertura JaCoCo
./mvnw test jacoco:report

# Reporte en: target/site/jacoco/index.html

🏗️ CI/CD Pipeline (GitHub Actions)

El pipeline (.github/workflows/ci-cd.yml) ejecuta:

    Build – Compila el backend y el frontend.

    Test – JUnit 5 + JaCoCo cobertura ≥80%.

    Docker Build – Construye imagen multi‑stage que incluye el frontend.

    Deploy – Push a DockerHub y despliegue por SSH (rama main).

📚 Patrones y Buenas Prácticas Aplicados
Backend

    ✅ Separación de capas: Controller → Service → Repository

    ✅ DTOs para no exponer entidades directamente

    ✅ Manejo global de errores (RFC 7807 Problem Details)

    ✅ Transacciones declarativas con @Transactional

    ✅ Consultas optimizadas (JOIN FETCH) para evitar problema N+1

    ✅ Eventos asíncronos via RabbitMQ

    ✅ Tests unitarios con Mockito (estilo BDD)

    ✅ Tests de integración con MockMvc

    ✅ Seguridad por roles con Spring Security y @PreAuthorize

    ✅ Paginación con Spring Data Pageable

    ✅ Logging estructurado con SLF4J/Logback

    ✅ Documentación automática con OpenAPI 3

Frontend

    ✅ Componentes funcionales con Hooks

    ✅ Tipado fuerte con TypeScript

    ✅ Autenticación persistente (localStorage) + interceptores de Axios

    ✅ Manejo de sesión mediante Context API

    ✅ Modo oscuro configurable y persistente

    ✅ Diseño responsivo con Tailwind CSS

    ✅ Mensajes de notificación (toast) para acciones del usuario

    ✅ Separación de servicios (api cliente, productService)

🔧 Últimas Mejoras / Cambios Recientes

    ✨ Integración del frontend React dentro del mismo contenedor Docker (archivos estáticos en /static).

    ✨ Corrección del DTO StockRequest – Ahora acepta operacion y cantidad para que el frontend pueda incrementar/reducir stock correctamente.

    ✨ Ajuste en el interceptor de Axios – No envía cabeceras Authorization en peticiones GET públicas (evita errores 401).

    ✨ Guardado de contraseña en localStorage – Permite reutilizar la autenticación en todas las peticiones protegidas.

    ✨ Healthcheck de Docker modificado de wget a curl para imágenes basadas en Alpine (compatible con JRE).

    ✨ Variables de entorno en docker-compose.yml para separar configuración sensible (se recomienda usar archivo .env ignorado por git).

📝 Licencia y Contribuciones

Este proyecto es de uso educativo. Puedes forkearlo, mejorarlo y adaptarlo libremente.

Si encuentras algún error o deseas proponer una mejora, por favor abre un issue o un pull request. ¡Las contribuciones son bienvenidas!

¿Qué habilidades cubre este proyecto?
Desde Spring Boot y Hibernate, pasando por Docker, CI/CD, APIs REST, mensajería asíncrona, testing unitario/de integración, hasta un frontend profesional en React. Es el portfolio definitivo para un desarrollador Java full‑stack.