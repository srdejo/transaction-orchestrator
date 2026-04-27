# ADR-0004: Idempotencia

**Date:** 2026-04-27
**Status:** Accepted

## Context
Para evitar el procesamiento duplicado de transacciones debido a reintentos de red o fallos en el cliente, se requiere un mecanismo que garantice que una misma petición solo se procese una vez.

## Decision
Implementar una capa de idempotencia utilizando **Redis** como almacenamiento distribuido de baja latencia. Se utiliza el `client_transaction_id` como clave para rastrear el estado de las peticiones (`IN_PROGRESS`, `COMPLETED`).

## Consequences
- **Pros**: Evita cobros dobles, maneja concurrencia de forma eficiente y ofrece respuestas rápidas para peticiones repetidas.
- **Cons**: Introduce una dependencia adicional (Redis) que debe ser gestionada y monitoreada.
