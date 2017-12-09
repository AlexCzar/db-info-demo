DROP DATABASE IF EXISTS dbinfo;
DROP ROLE IF EXISTS dbinfo;

CREATE USER dbinfo
  ENCRYPTED PASSWORD 'dbinfo';
CREATE DATABASE dbinfo OWNER dbinfo;

\c dbinfo
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO dbinfo;

CREATE TABLE products (
  id          INTEGER PRIMARY KEY,
  name        VARCHAR(20),
  description TEXT,
  price       NUMERIC
);

CREATE TABLE orders (
  order_id   INTEGER PRIMARY KEY,
  product_id INTEGER REFERENCES products (id),
  quantity   INTEGER
);

INSERT INTO products (id, name, description, price)
VALUES (1, 'Big Blue Book', 'Book about domain driven development', 100.00);
INSERT INTO orders (order_id, product_id, quantity) VALUES (1, 1, 1);
INSERT INTO orders (order_id, product_id, quantity) VALUES (2, 1, 3);
INSERT INTO orders (order_id, product_id, quantity) VALUES (3, 1, 2);