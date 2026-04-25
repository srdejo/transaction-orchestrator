# ADR-0004: Idempotencia con Redis

## Status
Accepted

## Context
Evitar transacciones duplicadas por reintentos (timeouts, retries, gateways). Se requiere baja latencia, control de concurrencia y expiración automática.

## Decision
Usar Redis con llave provista por el cliente (`X-Idempotency-Key`) y estados:
- IN_PROGRESS
- COMPLETED

Se valida además un `request_hash`.

## Flow
1. Buscar key en Redis
2. Si no existe:
   - SET NX (IN_PROGRESS)
   - Procesar
   - Guardar respuesta (COMPLETED)
3. Si existe:
   - Hash distinto → error
   - COMPLETED → respuesta cacheada
   - IN_PROGRESS → 409 / 202

## Redis Model
Key: `idempotency:{key}`

Value:
{
  request_hash,
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
