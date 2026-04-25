# ADR-0008: PostgreSQL vs H2

## Status

Accepted

## Context

Se requiere una base de datos consistente con entornos productivos y comportamiento real de SQL.

## Decision

Usar **PostgreSQL** como base principal.
H2 se descarta para evitar diferencias de comportamiento entre ambientes.

## Pros

* Consistencia con producción
* Soporte completo de SQL
* Evita errores por diferencias de motor

## Alternative

* H2: útil para testing rápido, pero no representativo en producción

## Consequences

Mayor fidelidad entre ambientes a costa de mayor setup inicial.
