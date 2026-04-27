# ADR-0001: Arquitectura Hexagonal

**Date:** 2026-04-27
**Status:** Accepted

## Context
Se requiere desacoplar la lógica de negocio de los frameworks y detalles de infraestructura, facilitando la integración con múltiples proveedores y permitiendo una evolución independiente del dominio.

## Decision
Adoptar la **Arquitectura Hexagonal (Ports & Adapters)**. La estructura se divide en:
- **Domain**: Modelos y puertos (interfaces).
- **Application**: Casos de uso.
- **Infrastructure**: Adaptadores (REST controllers, persistence, payment providers).

## Consequences
- **Pros**: Alto desacoplamiento, facilidad de testing unitario y flexibilidad para cambiar implementaciones externas.
- **Cons**: Mayor cantidad de clases y complejidad inicial en el mapeo entre capas.
