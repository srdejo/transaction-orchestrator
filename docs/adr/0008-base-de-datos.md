# ADR-0008: Base de Datos

**Date:** 2026-04-27
**Status:** Accepted

## Context
Se requiere un sistema de gestión de bases de datos relacionales que sea robusto, soporte tipos de datos avanzados (como UUID y JSONB) y garantice la paridad entre entornos.

## Decision
Utilizar **PostgreSQL** como motor de base de datos principal, evitando el uso de bases de datos en memoria (como H2) incluso para pruebas, para asegurar que el comportamiento del sistema sea idéntico en todos los ambientes.

## Consequences
- **Pros**: Soporte nativo para tipos de datos complejos, consistencia garantizada y herramientas de administración maduras.
- **Cons**: Requiere la gestión de un servidor de base de datos externo o contenedores, incluso en el entorno de desarrollo local.
