# ADR-0007: Trunk-Based Development

## Status

Accepted

## Context

Se requiere integración continua, entregas rápidas y bajo overhead en branching.

## Decision

Adoptar **Trunk-Based Development**: cambios integrados frecuentemente a `main` mediante ramas cortas.

## Flow

* Branch corto desde `main`
* PR + CI (build/test)
* Merge a `main`

## Guidelines

* Branches < 2 días
* Commits pequeños
* Uso de feature flags
* `main` siempre deployable

## Pros

* Menos conflictos
* Feedback rápido
* Flujo simple

## Cons

* Requiere disciplina
* Dependencia de CI/CD

## Alternative

* Git Flow: descartado por complejidad

## Consequences

Mejora velocidad de entrega y simplicidad operativa.
