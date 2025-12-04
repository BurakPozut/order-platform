-- Dummy payment records using real order_ids and random UUIDs for payment IDs

INSERT INTO payments (id, order_id, amount, currency, status, provider, provider_ref, created_at, updated_at) VALUES
(gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440003', 100.00, 'USD', 'COMPLETED', 'Stripe', 'STRP-101', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(gen_random_uuid(), '2b21a60f-2def-4ee9-a113-6eab5be5fb5d',  55.25, 'EUR', 'PENDING',   'PayPal', 'PYPL-202', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
(gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440001', 245.99, 'USD', 'FAILED',    'Stripe', 'STRP-303', NOW() - INTERVAL '1 day',  NOW() - INTERVAL '1 day'),
(gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440003',  10.00, 'GBP', 'COMPLETED', 'Square', 'SQ-404',   NOW()                  , NOW()),
(gen_random_uuid(), '2b21a60f-2def-4ee9-a113-6eab5be5fb5d',  77.50, 'USD', 'REFUNDED',  'PayPal', 'PYPL-505', NOW() - INTERVAL '8 hours', NOW() - INTERVAL '8 hours');
