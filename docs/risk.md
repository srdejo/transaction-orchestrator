# Risks

## Network Security
El servicio asume que un API Gateway externo gestiona la autenticación, rate limiting y protección contra ataques.
**Riesgo:** Exposición si el gateway está mal configurado.

---

## Data Consistency
Desfase entre el estado del proveedor de pagos y nuestra base de datos por fallos de red post-procesamiento.
**Riesgo:** Transacciones marcadas como fallidas que fueron exitosas en el proveedor.

---

## Sensitive Data Exposure
Manejo de PII (Personal Identifiable Information) de clientes.
**Riesgo:** Filtración de datos privados en logs o bases de datos no cifradas, incumpliendo normativas.

---

## Secrets Management
Uso de API Keys para integraciones con proveedores.
**Riesgo:** Exposición de credenciales si se almacenan en texto plano en configuraciones o variables de entorno.

---

## External Dependencies
Dependencia de la disponibilidad y latencia de APIs de terceros (Stripe, PayPal, etc.).
**Riesgo:** Degradación del servicio o fallos en cascada ante caídas del proveedor.

---

## Idempotency (Redis)
Uso de Redis para evitar transacciones duplicadas.
**Riesgo:** Pérdida de la capacidad de control de duplicados si Redis no está disponible (Single Point of Failure).
