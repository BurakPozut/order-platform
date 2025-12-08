CREATE TABLE orders(
  id            UUID PRIMARY KEY,
  customer_id   UUID  NOT NULL,
  status        VARCHAR(32) NOT NULL,
  total_amount  NUMERIC(10,2) NOT NULL,
  currency      CHAR(3) NOT NULL DEFAULT 'USD',
  created_at TIMESTAMP  NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP  NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items(
  id            UUID PRIMARY KEY,
  order_id      UUID    NOT NULL,
  product_id    UUID    NOT NULL,
  product_name  VARCHAR(255) NOT NULL,
  unit_price    NUMERIC(10,2) NOT NULL,
  quantity      INT   NOT NULL,
  CONSTRAINT fk_items_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
); 