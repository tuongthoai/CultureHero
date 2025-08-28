DROP TABLE IF EXISTS user_tokens;

CREATE TABLE user_tokens (
                             id UUID PRIMARY KEY,
                             user_id UUID NOT NULL REFERENCES users(id),
                             token VARCHAR(255) NOT NULL UNIQUE,
                             created_at TIMESTAMP NOT NULL,
                             expires_at TIMESTAMP NOT NULL
);
