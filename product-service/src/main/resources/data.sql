INSERT INTO products (id, name, price, currency, status) 
VALUES 
  (gen_random_uuid(), 'Laptop Pro 15', 1299.99, 'USD', 'ACTIVE'),
  (gen_random_uuid(), 'Wireless Mouse', 29.99, 'USD', 'ACTIVE'),
  (gen_random_uuid(), 'Mechanical Keyboard', 149.99, 'USD', 'ACTIVE'),
  (gen_random_uuid(), 'USB-C Hub', 79.99, 'EUR', 'ACTIVE'),
  (gen_random_uuid(), 'Monitor 27" 4K', 399.99, 'GBP', 'ACTIVE'),
  (gen_random_uuid(), 'Webcam HD', 89.99, 'USD', 'INACTIVE'),
  (gen_random_uuid(), 'Noise Cancelling Headphones', 299.99, 'USD', 'ACTIVE'),
  (gen_random_uuid(), 'SSD 1TB', 129.99, 'EUR', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;