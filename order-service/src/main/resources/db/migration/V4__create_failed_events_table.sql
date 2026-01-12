CREATE TABLE failed_events(
  id            UUID PRIMARY KEY,
  entity_id     UUID,  -- NULLable, can be order_id, customer_id, product_id, etc.
  entity_type   VARCHAR(50),  -- 'ORDER', 'CUSTOMER', 'PRODUCT', etc.
  event_type    VARCHAR(100) NOT NULL,
  event_payload TEXT NOT NULL,
  error_message VARCHAR(1000),
  status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  retry_count   INT NOT NULL DEFAULT 0,
  last_retry_at TIMESTAMP,
  created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_failed_events_status ON failed_events(status);
CREATE INDEX idx_failed_events_entity ON failed_events(entity_type, entity_id);
CREATE INDEX idx_failed_events_created_at ON failed_events(created_at);