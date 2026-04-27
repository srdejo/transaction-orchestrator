# ADR-0003: Migraciones con Flyway

**Date:** 2026-04-27
**Status:** Accepted

## Context
Se requiere versionar el esquema de la base de datos para garantizar la reproducibilidad entre ambientes y evitar cambios manuales que comprometan la integridad de los datos.

## Decision
Utilizar **Flyway** para la gestión de migraciones SQL. Las migraciones se almacenan en `src/main/resources/db/migration` y se ejecutan automáticamente al iniciar la aplicación.

## Consequences
- **Pros**: Control total sobre el esquema, histórico de cambios versionado en Git y prevención de inconsistencias mediante checksums.
- **Cons**: Requiere disciplina en la creación de scripts y manejo manual del SQL en lugar de depender totalmente del ORM.
