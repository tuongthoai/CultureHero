DROP TABLE IF EXISTS tasks;
CREATE TABLE tasks
(
    id           UUID PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    reward_xp    INT          NOT NULL,
    reward_coins INT          NOT NULL,
    difficulty   VARCHAR(20)  NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);
