# ADR-0002: OpenAPI Contract-First

**Date:** 2026-04-27
**Status:** Accepted

## Context
Es necesario garantizar la consistencia entre la especificación de la API y su implementación, además de facilitar la colaboración entre equipos mediante contratos claros.

## Decision
Utilizar un enfoque **Contract-First** con **OpenAPI 3.0**. La especificación (`openapi.yaml`) actúa como fuente única de verdad, y se utiliza `openapi-generator` para generar automáticamente los DTOs e interfaces de los controladores.

## Consequences
- **Pros**: Contratos consistentes, documentación (Swagger UI) siempre actualizada y ahorro de tiempo en la creación de modelos de transporte.
- **Cons**: Dependencia de la generación de código y necesidad de conocer la sintaxis de YAML/OpenAPI.
