INSERT INTO notifications (id, customer_id, order_id, type, channel, status, created_at) VALUES
  (gen_random_uuid(), 'cb8f1671-999e-43cc-8b56-8a561bf92a3e', '0a80b656-896f-48c6-9fa9-96cffae07cf0', 'ORDER_CONFIRMED', 'EMAIL', 'PENDING', NOW()),
  (gen_random_uuid(), '33662229-9d16-47c4-b4ef-ee9bd5a25983', '2b21a60f-2def-4ee9-a113-6eab5be5fb5d', 'ORDER_SHIPPED', 'SMS', 'SENT', NOW() - INTERVAL '1 day'),
  (gen_random_uuid(), 'cb8f1671-999e-43cc-8b56-8a561bf92a3e', '15eb6971-0ad2-4ab6-bd40-581776724032', 'ORDER_CONFIRMED', 'PUSH', 'FAILED', NOW() - INTERVAL '2 days'),
  (gen_random_uuid(), '33662229-9d16-47c4-b4ef-ee9bd5a25983', '0a80b656-896f-48c6-9fa9-96cffae07cf0', 'ORDER_CONFIRMED', 'EMAIL', 'SENT', NOW() - INTERVAL '3 days'),
  (gen_random_uuid(), 'cb8f1671-999e-43cc-8b56-8a561bf92a3e', '2b21a60f-2def-4ee9-a113-6eab5be5fb5d', 'ORDER_SHIPPED', 'SMS', 'PENDING', NOW());

