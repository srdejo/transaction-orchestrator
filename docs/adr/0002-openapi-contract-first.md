# ADR-0002: OpenAPI Contract-First

## Status
Accepted

## Context

Se requiere definir contratos API antes de implementar y mantener consistencia entre especificación y código.

## Decision

Usar **OpenAPI 3.0** como fuente única de verdad y generar código automáticamente.

## Flow

```id="g2l9bk"
openapi.yaml → generate → interfaces/DTOs → implementación
```

## Setup

* Archivo: `src/main/resources/openapi.yaml`
* Generación: `./gradlew openApiGenerate`
* Código generado en `build/`

## Pros

* Contrato claro y versionado
* Documentación automática
* Consistencia entre API e implementación

## Consequences

El contrato guía el desarrollo y evita desalineaciones entre equipos.
