# ADR-0001: Arquitectura Hexagonal

## Status
Accepted

## Context

Se requiere desacoplar la lógica de negocio de frameworks y facilitar la integración con múltiples proveedores.

## Decision

Adoptar **Arquitectura Hexagonal (Ports & Adapters)** para aislar el dominio de dependencias externas.

## Structure

```
domain/        # Modelo + puertos
application/   # Casos de uso
infrastructure/ # Adaptadores (REST, DB, providers)
```

## Pros

* Desacoplamiento del dominio
* Facilidad de testing
* Flexibilidad para cambiar integraciones

## Alternative

* Arquitectura en capas: mayor acoplamiento

## Consequences

El dominio permanece independiente y el sistema es más mantenible y extensible.
