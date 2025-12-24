ALTER TABLE payments
ADD COLUMN idempotency_key VARCHAR(128);

-- backfill for existing rows
UPDATE payments
SET idempotency_key = gen_random_uuid()::text
WHERE idempotency_key IS NULL;

ALTER TABLE payments
ALTER COLUMN idempotency_key SET NOT NULL;

CREATE UNIQUE INDEX ux_payments_idempotency_key
ON payments(order_id, idempotency_key);
