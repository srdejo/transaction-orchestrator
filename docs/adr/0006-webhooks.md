# ADR-0006: Manejo de Webhooks

## Status

Accepted

## Context

Los proveedores envían eventos asíncronos (pagos, fallos, reembolsos). Se requiere validación, idempotencia y soporte multi-proveedor.

## Decision

Implementar webhooks con **validación de firma** e **idempotencia** por evento.

## Flow

```id="z9k2qs"
POST /webhooks/providers/{provider}
→ validar firma
→ procesar evento
→ actualizar transacción
→ 200 OK
```

## Idempotency

Eventos únicos por `(provider, provider_event_id)` para evitar duplicados.

## Response

* 200 → procesado o duplicado
* 400 → payload inválido
* 403 → firma inválida
* 5xx → reintento del proveedor

## Pros

* Seguro y desacoplado
* Tolerante a reintentos
* Soporta múltiples proveedores

## Alternative

* Polling: alta latencia

## Consequences

Procesamiento confiable de eventos externos con control de duplicados.
