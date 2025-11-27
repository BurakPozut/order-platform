-- Insert dummy customer
INSERT INTO customers (id, email, full_name, status) 
VALUES (gen_random_uuid(), 'john.doe@example.com', 'John Doe', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;