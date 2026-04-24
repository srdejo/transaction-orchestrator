# ADR-0003: Migraciones de Base de Datos con Flyway

## Status
Accepted

## Context

Se requiere versionar el schema, garantizar reproducibilidad entre ambientes y evitar cambios manuales.

## Decision

Usar **Flyway** para gestionar migraciones SQL versionadas junto al código.

## Setup

* Ubicación: `db/migration`
* Convención: `V{n}__descripcion.sql`
* Ejecución automática al iniciar la aplicación

## Pros

* Versionado en Git
* Reproducible
* Control de cambios (checksum)

## Alternative

* JPA DDL / scripts manuales: descartados por falta de control

## Consequences

El schema evoluciona de forma controlada y consistente entre ambientes.
