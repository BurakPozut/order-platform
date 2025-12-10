DROP INDEX IF EXISTS ux_payments_idempotency_key;
ALTER TABLE payments DROP COLUMN IF EXISTS idempotency_key;

CREATE UNIQUE INDEX ux_payments_order_id ON payments(order_id);