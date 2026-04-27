# Transaction Orchestrator

Backend para la orquestación de transacciones de pago con soporte multi-proveedor. Este microservicio actúa como intermediario entre los clientes y diversos procesadores de pago (Stripe, PayPal, etc.), garantizando idempotencia, persistencia y trazabilidad mediante una arquitectura desacoplada.

---

## Requisitos Previos

Para levantar el sistema de forma local, es necesario contar con:
* Docker (versión 20.10+)
* Docker Compose (versión 2.0+)
* Git

---

## Instalación y Ejecución

Sigue estos pasos para iniciar los servicios (API, PostgreSQL, Redis):

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/srdejo/transaction-orchestrator.git
   cd transaction-orchestrator
   ```

2. **Levantar los contenedores:**
   ```bash
   docker-compose up --build
   ```

3. **Verificación:**
   La aplicación estará disponible en `http://localhost:8080`.

---

## Uso y Documentación de la API

Una vez iniciados los contenedores, se puede interactuar con la API y consultar su documentación técnica:

* **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **Especificación OpenAPI:** [openapi.yaml](transaction-orchestrator/src/main/resources/openapi.yaml)

---

## Arquitectura y Diseño

El proyecto implementa principios de ingeniería para asegurar escalabilidad y mantenibilidad:

* **Arquitectura Hexagonal:** Aislamiento del dominio frente a detalles de infraestructura.
* **OpenAPI Contract-First:** Definición formal de la API previa al desarrollo.
* **Idempotencia:** Control de duplicados en tiempo real mediante Redis.
* **Persistencia:** PostgreSQL con gestión de migraciones mediante Flyway.

### Documentación Técnica Relacionada
* [Registro de Decisiones Arquitectónicas (ADRs)](docs/adr/README.md)
* [Patrones de Diseño Aplicados](docs/design-patterns.md)
* [Análisis de Riesgos](docs/risk.md)
* [Estrategia de CI/CD](docs/ci-cd.md)

---

## Diagramas

* [Diagrama de Contexto](docs/diagrams/1_context.png)
* [Diagrama de Contenedores](docs/diagrams/2_container.png)
* [Diagrama de Componentes](docs/diagrams/3_component.png)
* [Diagrama de Secuencia](docs/diagrams/4_sequence.png)

*(Los fuentes editables se encuentran en la carpeta [docs/diagrams/code/](docs/diagrams/code/))*

---

## Calidad y Pruebas

La estrategia de validación está diseñada para garantizar estabilidad en las integraciones:
* **Pruebas de Integración:** Uso de Testcontainers para PostgreSQL y Redis reales.
* **Cobertura de Código:** Verificación automática mediante JaCoCo (mínimo 80%).

Para ejecutar el set de pruebas localmente:
```bash
cd transaction-orchestrator
./gradlew test
```

---

## Notas
El proyecto sigue el modelo de **Trunk-Based Development**. La lógica de negocio está centralizada en la capa de `domain`, protegida de cambios en frameworks externos o variaciones en las APIs de los proveedores de pago.
