-- Insert dummy orders
-- Note: customer_id should match an actual customer ID from customer-service
-- You may need to update the customer_id below to match an existing customer

-- Order 1: PENDING order
INSERT INTO orders (id, customer_id, status, total_amount, currency) 
VALUES 
  ('550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 'PENDING', 1329.98, 'USD')
ON CONFLICT (id) DO NOTHING;

-- Order 2: CONFIRMED order
INSERT INTO orders (id, customer_id, status, total_amount, currency) 
VALUES 
  ('550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', 'CONFIRMED', 179.98, 'USD')
ON CONFLICT (id) DO NOTHING;

-- Order 3: COMPLETED order
INSERT INTO orders (id, customer_id, status, total_amount, currency) 
VALUES 
  ('550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440000', 'COMPLETED', 149.99, 'USD')
ON CONFLICT (id) DO NOTHING;

-- Order 4: CANCELLED order
INSERT INTO orders (id, customer_id, status, total_amount, currency) 
VALUES 
  ('550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440000', 'CANCELLED', 29.99, 'USD')
ON CONFLICT (id) DO NOTHING;

-- Insert dummy order items using the provided product IDs
-- Product IDs:
-- d28e3a8b-4630-403b-86d1-2ea0137bf35f
-- c07b9205-f84d-4154-9067-a899158a1fec
-- 3b265dee-0274-45c9-8c46-7b9730354004

-- Order items for Order 1 (PENDING) - using product ID: d28e3a8b-4630-403b-86d1-2ea0137bf35f
INSERT INTO order_items (id, order_id, product_id, product_name, unit_price, quantity) 
VALUES 
  ('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'd28e3a8b-4630-403b-86d1-2ea0137bf35f', 'Laptop Pro 15', 1299.99, 1),
  ('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'c07b9205-f84d-4154-9067-a899158a1fec', 'Wireless Mouse', 29.99, 1)
ON CONFLICT (id) DO NOTHING;

-- Order items for Order 2 (CONFIRMED) - using product ID: c07b9205-f84d-4154-9067-a899158a1fec
INSERT INTO order_items (id, order_id, product_id, product_name, unit_price, quantity) 
VALUES 
  ('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440002', 'c07b9205-f84d-4154-9067-a899158a1fec', 'Wireless Mouse', 29.99, 1),
  ('660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440002', '3b265dee-0274-45c9-8c46-7b9730354004', 'Mechanical Keyboard', 149.99, 1)
ON CONFLICT (id) DO NOTHING;

-- Order items for Order 3 (COMPLETED) - using product ID: 3b265dee-0274-45c9-8c46-7b9730354004
INSERT INTO order_items (id, order_id, product_id, product_name, unit_price, quantity) 
VALUES 
  ('660e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440003', '3b265dee-0274-45c9-8c46-7b9730354004', 'Mechanical Keyboard', 149.99, 1)
ON CONFLICT (id) DO NOTHING;

-- Order items for Order 4 (CANCELLED) - using product ID: d28e3a8b-4630-403b-86d1-2ea0137bf35f
INSERT INTO order_items (id, order_id, product_id, product_name, unit_price, quantity) 
VALUES 
  ('660e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440004', 'd28e3a8b-4630-403b-86d1-2ea0137bf35f', 'Laptop Pro 15', 1299.99, 1)
ON CONFLICT (id) DO NOTHING;