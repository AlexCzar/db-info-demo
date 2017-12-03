CREATE USER dbinfo
  ENCRYPTED PASSWORD 'dbinfo';
CREATE DATABASE dbinfo OWNER dbinfo;

\c dbinfo

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
