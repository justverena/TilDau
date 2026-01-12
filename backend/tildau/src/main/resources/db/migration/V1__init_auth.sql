CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE role (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO role (id, name) VALUES (1, 'admin'), (2, 'user'), (3, 'doctor');

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role_id INTEGER REFERENCES role(id) NOT NULL DEFAULT 2,
                       avatar_url TEXT,
                       created_at TIMESTAMP DEFAULT now(),
                       updated_at TIMESTAMP DEFAULT now()
);