-- Insert dummy customer
INSERT INTO customers (id, email, full_name, status) 
VALUES (gen_random_uuid(), 'john.doe@example.com', 'John Doe', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;

-- Insert dummy product
INSERT INTO products (id, name, price, currency, status) 
SELECT gen_random_uuid(), 'Laptop', 999.99, 'USD', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Laptop'
);

-- Insert dummy order (using the customer we just inserted)
INSERT INTO orders (id, customer_id, status, total_amount, currency)
SELECT gen_random_uuid(), c.id, 'PENDING', 999.99, 'USD'
FROM customers c
WHERE c.email = 'john.doe@example.com'
  AND NOT EXISTS (
      SELECT 1 FROM orders o 
      WHERE o.customer_id = c.id 
        AND o.status = 'PENDING' 
        AND o.total_amount = 999.99
  )
LIMIT 1;

-- Insert dummy order item
INSERT INTO order_items (id, order_id, product_id, product_name, unit_price, quantity)
SELECT gen_random_uuid(), o.id, p.id, p.name, p.price, 1
FROM orders o, products p
WHERE o.customer_id = (SELECT id FROM customers WHERE email = 'john.doe@example.com' LIMIT 1)
  AND p.name = 'Laptop'
  AND NOT EXISTS (
      SELECT 1 FROM order_items oi 
      WHERE oi.order_id = o.id 
        AND oi.product_id = p.id
  )
LIMIT 1;

-- Insert dummy payment
INSERT INTO payments (id, order_id, amount, currency, status, provider, provider_ref)
SELECT gen_random_uuid(), o.id, o.total_amount, o.currency, 'COMPLETED', 'STRIPE', 'ch_test_1234567890'
FROM orders o
WHERE o.customer_id = (SELECT id FROM customers WHERE email = 'john.doe@example.com' LIMIT 1)
  AND NOT EXISTS (
      SELECT 1 FROM payments p 
      WHERE p.order_id = o.id 
        AND p.provider_ref = 'ch_test_1234567890'
  )
LIMIT 1;

-- Insert dummy notification
INSERT INTO notifications (id, customer_id, order_id, type, channed, status)
SELECT gen_random_uuid(), c.id, o.id, 'ORDER_CONFIRMED', 'EMAIL', 'SENT'
FROM customers c, orders o
WHERE c.email = 'john.doe@example.com'
  AND o.customer_id = c.id
  AND NOT EXISTS (
      SELECT 1 FROM notifications n 
      WHERE n.customer_id = c.id 
        AND n.order_id = o.id 
        AND n.type = 'ORDER_CONFIRMED'
  )
LIMIT 1;