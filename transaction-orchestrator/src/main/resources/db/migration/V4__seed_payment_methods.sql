-- =========================
-- PAYMENT METHODS
-- =========================
INSERT INTO payment_methods (id, code, description, is_active)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'CARD', 'Tarjeta de Crédito/Débito', TRUE),
    ('550e8400-e29b-41d4-a716-446655440002', 'PSE', 'Pagos Electrónicos Seguros (Colombia)', TRUE),
    ('550e8400-e29b-41d4-a716-446655440003', 'PAYPAL', 'PayPal', TRUE);
