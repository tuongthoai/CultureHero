DROP TABLE IF EXISTS streaks;
CREATE TABLE streaks
(
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL UNIQUE REFERENCES users (id),
    count           INT  NOT NULL,
    last_login_date DATE NOT NULL
);