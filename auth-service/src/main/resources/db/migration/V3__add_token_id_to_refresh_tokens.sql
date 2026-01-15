-- Add token_id column for fast lookup
ALTER TABLE refresh_tokens 
ADD COLUMN token_id VARCHAR(255);

-- Create unique index for fast lookup
CREATE UNIQUE INDEX idx_refresh_tokens_token_id ON refresh_tokens(token_id);

-- Make it NOT NULL (if you have existing data, you'll need to populate it first)
-- For new installations, this is fine
ALTER TABLE refresh_tokens 
ALTER COLUMN token_id SET NOT NULL;