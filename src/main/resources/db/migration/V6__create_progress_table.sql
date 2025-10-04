DROP TABLE IF EXISTS progress;
CREATE TABLE progress
(
    id               UUID PRIMARY KEY,
    user_id          UUID      NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    xp               INT       NOT NULL DEFAULT 0,
    level            INT       NOT NULL DEFAULT 1,
    streak           INT       NOT NULL DEFAULT 0,
    last_streak_date TIMESTAMP,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);
