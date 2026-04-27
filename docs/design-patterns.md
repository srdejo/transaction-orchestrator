# Design Patterns

## Hexagonal Architecture (Ports & Adapters)

Aísla el dominio de dependencias externas mediante puertos y adaptadores, facilitando la evolución independiente y el testing.

## Dependency Injection

Gestionado por Spring Framework para desacoplar la creación de objetos de su uso, mejorando la mantenibilidad.

## Strategy Pattern

Implementado para seleccionar dinámicamente el proveedor de pago adecuado según el método de pago o configuración.

## Factory Pattern

Encapsula la lógica de instanciación de los adaptadores de proveedores de pago (`PaymentProviderFactory`).

## Adapter Pattern

Permite que el sistema interactúe con APIs de terceros (Stripe, PayPal, etc.) traduciendo sus interfaces a un modelo interno común (`PaymentProviderPort`).

## Idempotency Pattern (AOP)

Implementado mediante **Programación Orientada a Aspectos (AOP)**. Se utiliza un interceptor (`IdempotencyAspect`) que rodea la ejecución de los controladores para verificar y persistir estados de transacciones en Redis.

## Repository Pattern

Abstrae la persistencia de datos mediante Spring Data JPA, permitiendo al dominio interactuar con la base de datos sin conocer los detalles de implementación SQL.
