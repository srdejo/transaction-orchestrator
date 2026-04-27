# ADR-0007: Trunk-Based Development

**Date:** 2026-04-27
**Status:** Accepted

## Context
Se busca agilizar el ciclo de entrega y reducir la complejidad de las integraciones de código (merge hell) típicas de modelos basados en ramas de larga duración.

## Decision
Adoptar **Trunk-Based Development**. Los desarrolladores integran cambios pequeños y frecuentes directamente en la rama principal (`main`), apoyándose en pipelines de CI automatizados para garantizar la estabilidad.

## Consequences
- **Pros**: Integración continua real, reducción de conflictos de código y feedback inmediato sobre la calidad del software.
- **Cons**: Requiere una suite de pruebas automatizadas robusta y disciplina de equipo para no romper la rama principal.
