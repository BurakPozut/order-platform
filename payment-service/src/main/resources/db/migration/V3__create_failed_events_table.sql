CREATE TABLE failed_events(
  id            UUID PRIMARY KEY,
  order_id      UUID NOT NULL,
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
CREATE INDEX idx_failed_events_order_id ON failed_events(order_id);
CREATE INDEX idx_failed_events_created_at ON failed_events(created_at);