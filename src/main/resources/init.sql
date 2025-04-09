CREATE TABLE IF NOT EXISTS subscriptions
(
    id            SERIAL PRIMARY KEY,
    type          VARCHAR(50)    NOT NULL,
    cost          DECIMAL(10, 2) NOT NULL,
    duration_days INT            NOT NULL
);

CREATE TABLE IF NOT EXISTS clients
(
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    subscription_id INT,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions (id)
);