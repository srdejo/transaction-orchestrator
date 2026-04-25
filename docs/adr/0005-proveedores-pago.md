# ADR 0005: Estrategia de Proveedores de Pago

## Estado
✅ Aceptado

## Contexto
Necesitar soportar múltiples proveedores:
- Stripe, PayPal, etc.
- Cambiar proveedores sin afectar lógica central
- Diferencias específicas entre proveedores

## Decisión
Usar **Strategy Pattern** con **Factory** para proveedores.

## Arquitectura
```
PaymentProviderPort (interfaz)
    ↑
    ├── StripePaymentProvider
    ├── PayPalPaymentProvider
    └── MockPaymentProvider
    
PaymentProviderFactory → selecciona proveedor
```

## Interfaz
```java
public interface PaymentProviderPort {
    TransactionResponse processPayment(PaymentRequest request);
    TransactionStatus checkStatus(String transactionId);
}
```

## Selección de Proveedor
```
payment_method_id: "CARD" → StripePaymentProvider
payment_method_id: "PAYPAL" → PayPalPaymentProvider
payment_method_id: "MOCK" → MockPaymentProvider (dev/test)
```

## Configuración
```properties
payment.providers.stripe.enabled=true
payment.providers.stripe.api-key=${STRIPE_API_KEY}

payment.providers.paypal.enabled=true
payment.providers.paypal.client-id=${PAYPAL_CLIENT_ID}

payment.providers.mock.enabled=true
```

## Ventajas
- ✅ Fácil agregar nuevos proveedores
- ✅ Lógica encapsulada por proveedor
- ✅ Fácil de testear con Mock
- ✅ Cambio de estrategia en runtime

## Checklist para Nuevo Proveedor
1. Crear clase implementando `PaymentProviderPort`
2. Implementar `processPayment()` y `checkStatus()`
3. Agregar propiedades de configuración
4. Registrar en `PaymentProviderFactory`
5. Tests de integración
6. Documentar límites y particularidades


## Documentos Relacionados
- ADR-0001: Arquitectura Hexagonal
- ADR-0006: Manejo de Webhooks
