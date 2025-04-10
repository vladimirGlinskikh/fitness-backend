DROP TABLE subscriptions CASCADE;
DROP TABLE clients CASCADE;

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

INSERT INTO subscriptions (type, cost, duration_days)
VALUES ('Стандарт', 5000.0, 30),
       ('Премиум', 10000.0, 90),
       ('Эконом', 3000.0, 15);

UPDATE clients
SET subscription_id = 1
WHERE id = 1; -- Клиент с id=1 получает абонемент "Стандарт"
UPDATE clients
SET subscription_id = 2
WHERE id = 2; -- Клиент с id=2 получает абонемент "Премиум"
UPDATE clients
SET subscription_id = 3
WHERE id = 3; -- Клиент с id=3 получает абонемент "Эконом"
UPDATE clients
SET subscription_id = 1
WHERE id = 4; -- Клиент с id=4 получает абонемент "Стандарт"