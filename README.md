# Transaction Orchestrator

Backend para orquestación de pagos con múltiples proveedores.
Diseñado con arquitectura hexagonal y enfoque contract-first.

---

## Arquitectura

* Hexagonal (Ports & Adapters)
* OpenAPI como contrato
* Idempotencia con Redis
* Migraciones con Flyway

Decisiones documentadas en:
docs/adr/

---

## Diagramas

Context:
docs/diagrams/1_context.puml

Container:
docs/diagrams/2_container.puml

Component:
docs/diagrams/3_component.puml

Sequence:
docs/diagrams/4_sequence.puml

---

## Patrones de diseño

Ver:
docs/design-patterns.md

---

## CI/CD

Ver:
docs/ci-cd.md

---

## Ejecución

```bash
docker-compose up --build
```

---

## Suposiciones

* Existe un API Gateway externo
* Los proveedores exponen APIs REST
* Redis disponible para idempotencia

---

## Riesgos

Ver:
docs/risks.md

---

## Notas

El proyecto sigue un enfoque contract-first y trunk-based development.
