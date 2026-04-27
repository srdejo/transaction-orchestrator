# ADR-0005: Proveedores de Pago

**Date:** 2026-04-27
**Status:** Accepted

## Context
El sistema debe ser capaz de integrarse con múltiples proveedores de pago (Stripe, PayPal, Mocks) y permitir el cambio o adición de nuevos proveedores sin afectar la lógica de negocio central.

## Decision
Aplicar el patrón **Strategy** junto con una **Factory** para gestionar los proveedores. Cada proveedor implementa un puerto común (`PaymentProviderPort`), y el sistema selecciona la implementación adecuada basándose en la configuración o el método de pago solicitado.

## Consequences
- **Pros**: Alta extensibilidad, facilidad para realizar pruebas con implementaciones Mock y aislamiento de las particularidades de cada API externa.
- **Cons**: Requiere una interfaz común que debe ser lo suficientemente flexible para cubrir las necesidades de diversos proveedores.
