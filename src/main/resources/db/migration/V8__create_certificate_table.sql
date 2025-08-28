DROP TABLE IF EXISTS certificates;
CREATE TABLE certificates (
                              id UUID PRIMARY KEY,
                              user_id UUID NOT NULL,
                              status VARCHAR(20) NOT NULL, -- REQUESTED, APPROVED, REJECTED
                              requested_at TIMESTAMP NOT NULL,
                              approved_at TIMESTAMP,
                              rejected_at TIMESTAMP,
                              reason TEXT,
                              CONSTRAINT fk_certificates_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
