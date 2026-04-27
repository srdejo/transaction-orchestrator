# Architectural Decision Records (ADRs)

Registro de decisiones arquitectónicas del proyecto, documentando el contexto, las decisiones tomadas y sus consecuencias.

## Índice

| ADR | Título | Estado |
| :--- | :--- | :--- |
| [0001](0001-arquitectura-hexagonal.md) | Arquitectura Hexagonal | ✅ Aceptado |
| [0002](0002-openapi-contract-first.md) | OpenAPI Contract-First | ✅ Aceptado |
| [0003](0003-flyway-migraciones.md) | Migraciones con Flyway | ✅ Aceptado |
| [0004](0004-idempotencia.md) | Idempotencia | ✅ Aceptado |
| [0005](0005-proveedores-pago.md) | Proveedores de Pago | ✅ Aceptado |
| [0006](0006-testing-strategy.md) | Estrategia de Pruebas | ✅ Aceptado |
| [0007](0007-trunk-based.md) | Trunk-Based Development | ✅ Aceptado |
| [0008](0008-base-de-datos.md) | Base de Datos | ✅ Aceptado |

## Formato de Documento

Cada registro sigue la siguiente estructura:
- **Context**: Descripción del problema o requerimiento.
- **Decision**: Detalle de la solución técnica adoptada.
- **Consequences**: Balance de ventajas y desventajas resultantes.

## Mantenimiento

- Los ADRs son inmutables.
- Nuevas decisiones deben crear un nuevo registro.
- Cambios significativos en una decisión previa deben documentarse en un nuevo ADR que sustituya al anterior.
