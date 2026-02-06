CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(60)         NOT NULL,
    last_name  VARCHAR(60)         NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   TEXT                NOT NULL,
    role       VARCHAR(25)         NOT NULL,
    enabled    BOOLEAN          DEFAULT false,
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_login TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tokens
(
    id         BIGSERIAL PRIMARY KEY,
    token_hash TEXT        NOT NULL,
    token_type VARCHAR(60) NOT NULL,
    used       BOOLEAN DEFAULT false,
    issued_at  TIMESTAMP,
    expired_at TIMESTAMP,
    user_id    UUID REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY
(
    conversation_id VARCHAR(36) NOT NULL,
    content         TEXT        NOT NULL,
    type            VARCHAR(10) NOT NULL CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL')),
    "timestamp"     TIMESTAMP   NOT NULL
);

CREATE INDEX IF NOT EXISTS SPRING_AI_CHAT_MEMORY_CONVERSATION_ID_TIMESTAMP_IDX
    ON SPRING_AI_CHAT_MEMORY (conversation_id, "timestamp");