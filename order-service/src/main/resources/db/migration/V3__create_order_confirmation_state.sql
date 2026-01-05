CREATE TABLE order_confirmation_state (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id UUID NOT NULL UNIQUE,
  payment_completed BOOLEAN NOT NULL DEFAULT FALSE,
  product_completed BOOLEAN NOT NULL DEFAULT FALSE,
  notification_completed BOOLEAN NOT NULL DEFAULT FALSE,
  confirmed_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_confirmation_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX idx_confirmation_order_id ON order_confirmation_state(order_id);