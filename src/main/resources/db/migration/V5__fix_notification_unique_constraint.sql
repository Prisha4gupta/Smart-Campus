-- Drop the unique constraint to allow multiple notifications per user with null event_id
-- We need to check if it exists or just use a standard drop for MySQL
-- Note: MySQL syntax to drop constraint/index
ALTER TABLE notifications DROP INDEX unique_user_event;
