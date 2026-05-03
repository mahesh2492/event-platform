CREATE TABLE events (
  event_id TEXT PRIMARY KEY,
  user_id TEXT,
  event_type TEXT,
  timestamp BIGINT,
  payload TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);