ALTER TABLE failed_events
  ADD COLUMN entity_id UUID,
  ADD COLUMN entity_type VARCHAR(50),
  ADD COLUMN event_class_name VARCHAR(255);

-- Update existing records if any
UPDATE failed_events 
SET entity_id = order_id,
    entity_type = 'ORDER',
    event_class_name = event_type
WHERE entity_id IS NULL;

-- Make event_class_name NOT NULL after backfilling
ALTER TABLE failed_events
  ALTER COLUMN event_class_name SET NOT NULL;

-- Drop the old order_id column (or keep it for backward compatibility)
-- ALTER TABLE failed_events DROP COLUMN order_id;

-- Add new index
CREATE INDEX IF NOT EXISTS idx_failed_events_entity ON failed_events(entity_type, entity_id);