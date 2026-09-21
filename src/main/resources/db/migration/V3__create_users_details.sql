CREATE TABLE users_details (
    user_id INTEGER PRIMARY KEY,
    nickname VARCHAR(60) UNIQUE NOT NULL,
    avatar_url TEXT NULL,
    role_id INTEGER,
    updated_at TIMESTAMP
);