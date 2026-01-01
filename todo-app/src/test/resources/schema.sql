-- Drop tables if they exist to avoid conflicts
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS todo;

-- Create todo table with H2-compatible syntax
CREATE TABLE IF NOT EXISTS todo (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    target_date DATE NOT NULL,
    done BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create a user's table with H2-compatible syntax
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create an authority's table with H2-compatible syntax
CREATE TABLE IF NOT EXISTS authorities (
    username VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NOT NULL,
    FOREIGN KEY(username) REFERENCES users(username),
    UNIQUE(username, authority)
);