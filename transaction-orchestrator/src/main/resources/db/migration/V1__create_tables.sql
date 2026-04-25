-- =========================
-- COUNTRIES
-- =========================
CREATE TABLE countries (
    country_code CHAR(2) PRIMARY KEY,   -- ISO 3166-1 alpha-2
    name VARCHAR(100) NOT NULL
);

-- =========================
-- CURRENCIES
-- =========================
CREATE TABLE currencies (
    currency_code CHAR(3) PRIMARY KEY,  -- ISO 4217
    name VARCHAR(100) NOT NULL
);

-- =========================
-- PAYMENT METHODS
-- =========================
CREATE TABLE payment_methods (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,   -- e.g. CARD, PAYPAL, PSE
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- CUSTOMERS
-- =========================
CREATE TABLE customers (
    id UUID PRIMARY KEY,

    document_type VARCHAR(30) NOT NULL,
    document_number VARCHAR(50) NOT NULL,

    country_calling_code VARCHAR(10),
    phone_number VARCHAR(30),

    email VARCHAR(150),

    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    second_last_name VARCHAR(100),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- TRANSACTION STATUS ENUM
-- =========================
CREATE TYPE transaction_status AS ENUM (
    'PENDING',
    'PROCESSING',
    'COMPLETED',
    'FAILED',
    'CANCELLED'
);

-- =========================
-- TRANSACTIONS
-- =========================
CREATE TABLE transactions (
    id UUID PRIMARY KEY,

    customer_transaction_id VARCHAR(100) NOT NULL,
    customer_id UUID NOT NULL,

    amount_cents BIGINT NOT NULL CHECK (amount_cents >= 0),

    currency_code CHAR(3) NOT NULL,
    country_code CHAR(2) NOT NULL,

    payment_method_id UUID NOT NULL,

    description VARCHAR(255),

    expiration_time TIMESTAMP,

    webhook_url TEXT NOT NULL,
    redirect_url TEXT NOT NULL,

    status transaction_status NOT NULL DEFAULT 'PENDING',

    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id),

    CONSTRAINT fk_currency
        FOREIGN KEY (currency_code)
        REFERENCES currencies(currency_code),

    CONSTRAINT fk_country
        FOREIGN KEY (country_code)
        REFERENCES countries(country_code),

    CONSTRAINT fk_payment_method
        FOREIGN KEY (payment_method_id)
        REFERENCES payment_methods(id)
);

CREATE UNIQUE INDEX ux_transactions_customer_transaction_id
ON transactions(customer_transaction_id);

CREATE INDEX idx_transactions_customer ON transactions(customer_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);