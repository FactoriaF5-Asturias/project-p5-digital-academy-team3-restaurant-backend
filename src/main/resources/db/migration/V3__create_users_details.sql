CREATE TABLE users_details (
    user_id INTEGER PRIMARY KEY,
    email VARCHAR(60) UNIQUE NOT NULL,
    avatar_url TEXT NULL,
    role_id INTEGER NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT fk_users_id_user
        FOREIGN KEY (user_id)
        REFERENCES users_auth(id),

    CONSTRAINT fk_users_details_role
        FOREIGN KEY (role_id)
        REFERENCES users_role(id)

);