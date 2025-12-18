CREATE TABLE products(
  id          UUID PRIMARY KEY,
  name        VARCHAR(255) UNIQUE NOT NULL,
  price       NUMERIC(10,2) NOT NULL,
  currency    CHAR(3)       NOT NULL DEFAULT 'USD',
  status      VARCHAR(32)   NOT NULL DEFAULT 'ACTIVE',
  version     BIGINT        NOT NULL DEFAULT 0,
  inventory   INT           NOT NULL DEFAULT 0,
  created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

INSERT INTO products (id, name, price, currency, status, inventory) 
VALUES 
  (gen_random_uuid(), 'Laptop Pro 15', 1299.99, 'USD', 'ACTIVE', 50),
  (gen_random_uuid(), 'Wireless Mouse', 29.99, 'USD', 'ACTIVE', 200),
  (gen_random_uuid(), 'Mechanical Keyboard', 149.99, 'USD', 'ACTIVE', 100),
  (gen_random_uuid(), 'USB-C Hub', 79.99, 'EUR', 'ACTIVE', 150),
  (gen_random_uuid(), 'Monitor 27" 4K', 399.99, 'GBP', 'ACTIVE', 30),
  (gen_random_uuid(), 'Webcam HD', 89.99, 'USD', 'INACTIVE', 0),
  (gen_random_uuid(), 'Noise Cancelling Headphones', 299.99, 'USD', 'ACTIVE', 75),
  (gen_random_uuid(), 'SSD 1TB', 129.99, 'EUR', 'ACTIVE', 120)
ON CONFLICT (name) DO NOTHING;