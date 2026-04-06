CREATE TABLE IF NOT EXISTS users (
    id            BIGINT          AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100)    NOT NULL,
    email         VARCHAR(150)    NOT NULL UNIQUE,
    password_hash VARCHAR(255)    NOT NULL,
    role          ENUM('VIEWER', 'ANALYST', 'ADMIN') NOT NULL DEFAULT 'VIEWER',
    status        ENUM('ACTIVE', 'INACTIVE')          NOT NULL DEFAULT 'ACTIVE',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
    id          BIGINT          AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT          NOT NULL,
    amount      DECIMAL(15, 2)  NOT NULL,
    type        ENUM('INCOME', 'EXPENSE') NOT NULL,
    category    VARCHAR(100)    NOT NULL,
    date        DATE            NOT NULL,
    notes       VARCHAR(500),
    is_deleted  BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(id)
);
