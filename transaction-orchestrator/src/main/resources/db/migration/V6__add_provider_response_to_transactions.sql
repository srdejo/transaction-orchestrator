-- =============================================
-- Add provider_response column to transactions
-- =============================================
ALTER TABLE transactions ADD COLUMN provider_response TEXT;
