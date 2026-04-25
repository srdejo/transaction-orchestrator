CREATE TABLE transactions (
    id                    UUID PRIMARY KEY,
    client_transaction_id VARCHAR(100) UNIQUE,
    amount                BIGINT NOT NULL,
    currency              VARCHAR(3),
    country               VARCHAR(2),
    status                VARCHAR(20),
    created_at            TIMESTAMP
);