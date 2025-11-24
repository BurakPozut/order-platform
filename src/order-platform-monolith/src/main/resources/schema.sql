CREATE TABLE IF NOT EXISTS customers(
  id        UUID PRIMARY KEY,
  email     VARCHAR(255) NOT NULL UNIQUE,
  full_name VARCHAR(255) NOT NULL,
  status    VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP  NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP  NOT NULL DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS products(
  id        UUID PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  price     NUMERIC(10,2) NOT NULL,
  currency  CHAR(3) NOT NULL DEFAULT 'USD',
  status    VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP  NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP  NOT NULL DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS orders(
  id            UUID PRIMARY KEY,
  customer_id   UUID  NOT NULL,
  status        VARCHAR(32) NOT NULL,
  total_amount  NUMERIC(10,2) NOT NULL,
  currency      CHAR(3) NOT NULL DEFAULT 'USD',
  created_at TIMESTAMP  NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP  NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_orders_customer
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);


CREATE TABLE IF NOT EXISTS order_items(
  id            UUID PRIMARY KEY,
  order_id      UUID    NOT NULL,
  product_id    UUID    NOT NULL,
  product_name  VARCHAR(255) NOT NULL,
  unit_price    NUMERIC(10,2) NOT NULL,
  quantity      INT   NOT NULL,
  CONSTRAINT fk_items_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
);


CREATE TABLE IF NOT EXISTS payments(
  id              UUID PRIMARY KEY,
  order_id        UUID       NOT NULL,
  amount          NUMERIC(10,2) NOT NULL,
  currency        CHAR(3)       NOT NULL DEFAULT 'USD',
  status          VARCHAR(32)   NOT NULL,
  provider        VARCHAR(64)   NOT NULL,
  provider_ref    VARCHAR(128),
  created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_payments_order
      FOREIGN KEY (order_id) REFERENCES orders(id)
);


CREATE TABLE IF NOT EXISTS notifications(
  id              UUID PRIMARY KEY,
  customer_id     UUID,
  order_id        UUID,
  type          VARCHAR(20) NOT NULL DEFAULT 'ORDER_CONFIRMED',
  channel       VARCHAR(20) NOT NULL,
  status        VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);
