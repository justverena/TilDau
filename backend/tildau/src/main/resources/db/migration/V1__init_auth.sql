CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE role (
                      id INTEGER PRIMARY KEY,
                      name VARCHAR(100) UNIQUE NOT NULL
);
CREATE TYPE account_status_enum AS ENUM ('ACTIVE', 'PENDING', 'BANNED');

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       surname VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role_id INTEGER REFERENCES role(id),
                       account_status account_status_enum
                           NOT NULL DEFAULT 'PENDING',
                       created_at TIMESTAMP DEFAULT now(),
                       updated_at TIMESTAMP DEFAULT now()
);

INSERT INTO role (id, name) VALUES (1, 'admin'), (2, 'user'), (3, 'logoped');

INSERT INTO users (id, surname, name, email, password, role_id, account_status)
VALUES (
           uuid_generate_v4(),
           'Admin',
            'Admin',
           'admin@example.com',
           '$2a$10$KX5jrXwHUH5D0tVsa8u/oepQ2AyoTpXmGezmZ2bBawC40IWMRk/Fu', --password: admin123, AdminPasswordHashTest
           1,
        'ACTIVE'
       );
