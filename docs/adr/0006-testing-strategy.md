# ADR-0006: Estrategia de Pruebas

**Date:** 2026-04-27
**Status:** Accepted

## Context
Se requiere garantizar la calidad del código y el correcto funcionamiento de las integraciones con bases de datos y servicios externos, manteniendo un alto nivel de cobertura y confiabilidad.

## Decision
Adoptar una estrategia de pruebas basada en:
1. **Testcontainers**: Para pruebas de integración, levantando instancias reales de PostgreSQL y Redis en Docker. Esto garantiza que las pruebas se ejecuten en un entorno idéntico al de producción.
2. **JaCoCo**: Para la medición de la cobertura de código, asegurando que los casos críticos estén debidamente probados.
3. **JUnit 5 & Mockito**: Para pruebas unitarias y de componentes aislados.

## Consequences
- **Pros**: Pruebas de integración confiables y reproducibles, métricas claras de calidad de código y detección temprana de fallos de integración.
- **Cons**: Mayor tiempo de ejecución de las pruebas debido al levantamiento de contenedores y requerimiento de Docker en el entorno de CI/CD.
