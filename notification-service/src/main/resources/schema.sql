CREATE TABLE IF NOT EXISTS notifications(
  id              UUID PRIMARY KEY,
  customer_id     UUID,
  order_id        UUID,
  type          VARCHAR(20) NOT NULL DEFAULT 'ORDER_CONFIRMED',
  channel       VARCHAR(20) NOT NULL,
  status        VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);
