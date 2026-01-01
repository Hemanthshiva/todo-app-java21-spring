-- Drop tables if they exist to avoid conflicts
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS todo_assignment;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS todo;

-- Create todo table with SQLite-compatible syntax
CREATE TABLE IF NOT EXISTS todo (
    id INTEGER PRIMARY KEY,
    username TEXT NOT NULL,
    description TEXT NOT NULL,
    target_date TEXT NOT NULL,
    done INTEGER NOT NULL DEFAULT 0
);

-- Create a user's table with SQLite-compatible syntax
CREATE TABLE IF NOT EXISTS users (
    username TEXT PRIMARY KEY,
    password TEXT NOT NULL,
    email TEXT,
    enabled INTEGER NOT NULL DEFAULT 1
);

-- Create an authority's table with SQLite-compatible syntax
CREATE TABLE IF NOT EXISTS authorities (
    username TEXT NOT NULL,
    authority TEXT NOT NULL,
    FOREIGN KEY(username) REFERENCES users(username),
    UNIQUE(username, authority)
);

-- Create todo_assignment table
CREATE TABLE IF NOT EXISTS todo_assignment (
    id INTEGER PRIMARY KEY,
    todo_id INTEGER NOT NULL,
    assigner_username TEXT NOT NULL,
    assignee_username TEXT NOT NULL,
    status TEXT NOT NULL,
    tentative_completion_date TEXT,
    decline_reason TEXT,
    assigned_at TEXT,
    responded_at TEXT,
    FOREIGN KEY(todo_id) REFERENCES todo(id),
    FOREIGN KEY(assigner_username) REFERENCES users(username),
    FOREIGN KEY(assignee_username) REFERENCES users(username)
);

-- Create notification table
CREATE TABLE IF NOT EXISTS notification (
    id INTEGER PRIMARY KEY,
    recipient_username TEXT NOT NULL,
    message TEXT NOT NULL,
    is_read INTEGER NOT NULL DEFAULT 0,
    related_todo_id INTEGER,
    created_at TEXT NOT NULL,
    FOREIGN KEY(recipient_username) REFERENCES users(username)
);
