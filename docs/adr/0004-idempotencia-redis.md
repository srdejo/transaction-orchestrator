# ADR-0004: Idempotencia con Redis

## Status
Accepted

## Context
Evitar transacciones duplicadas por reintentos (timeouts, retries, gateways). Se requiere baja latencia, control de concurrencia y expiración automática.

## Decision
Usar Redis como capa de idempotencia de corto plazo, utilizando el identificador único provisto en el payload (`external_custom_id`) como llave, manejando los estados:
- IN_PROGRESS
- COMPLETED

(La integridad a largo plazo se garantiza mediante un índice único en la BD sobre `customer_transaction_id`).

## Flow
1. Extraer `external_custom_id` de la petición.
2. Buscar la llave en Redis.
3. Si no existe:
   - SET NX (IN_PROGRESS)
   - Procesar la transacción
   - Guardar respuesta (COMPLETED)
4. Si existe:
   - COMPLETED → devolver respuesta cacheada
   - IN_PROGRESS → devolver HTTP 409 Conflict

## Redis Model
Key: `idempotency:tx:{external_custom_id}`

Value:
{
  status,
  response
}

## Config
- TTL: 24h
- SET NX EX

## Pros
- Baja latencia
- Escalable
- Evita duplicados concurrentes

## Cons
- Dependencia de Redis
- No persistente por defecto

## Alternatives
- DB: más lenta
- Sin idempotencia: no viable

## Consequences
Se mejora resiliencia y consistencia en pagos.
