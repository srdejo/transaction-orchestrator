# Risks

## Network Security

El servicio no implementa mecanismos de autenticación/autorización propios.
Se asume que un API Gateway externo se encarga de:

* Autenticación
* Rate limiting
* Protección contra ataques

Riesgo:
Si el gateway no está correctamente configurado, el servicio podría quedar expuesto.

---

## Webhook Security

Los webhooks dependen de:

* HTTPS para transporte seguro
* Validación de firma (a nivel aplicación)
* Seguridad de infraestructura (firewalls, red privada, etc.)

Riesgo:
La seguridad depende parcialmente de la configuración externa (infraestructura y proveedor).

---

## External Dependencies

Dependencia de proveedores de pago externos.

Riesgo:

* Latencia
* Fallos
* Cambios en APIs externas

---

## Idempotency (Redis)

Dependencia de Redis para control de idempotencia.

Riesgo:

* Pérdida de datos si Redis falla
* Inconsistencias en escenarios de error
