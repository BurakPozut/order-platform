CREATE TABLE IF NOT EXISTS payments(
  id              UUID PRIMARY KEY,
  order_id        UUID       NOT NULL,
  amount          NUMERIC(10,2) NOT NULL,
  currency        CHAR(3)       NOT NULL DEFAULT 'USD',
  status          VARCHAR(32)   NOT NULL,
  provider        VARCHAR(64)   NOT NULL,
  provider_ref    VARCHAR(128),
  created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);
