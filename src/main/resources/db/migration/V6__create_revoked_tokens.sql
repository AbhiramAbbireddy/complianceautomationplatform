CREATE TABLE revoked_tokens (

    id BIGSERIAL PRIMARY KEY,

    token_jti VARCHAR(255) NOT NULL UNIQUE,

    revoked_at TIMESTAMP NOT NULL,

    user_email VARCHAR(255) NOT NULL
);