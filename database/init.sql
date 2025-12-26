-- USERS
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- PROJECTS
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_project_owner
        FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- TASKS
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    due_date DATE,
    project_id BIGINT NOT NULL,
    CONSTRAINT fk_task_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
);

-- INITIAL USERS
INSERT INTO users (first_name, last_name, username, email, password, role)
VALUES
(
  'Admin',
  'User',
  'admin',
  'admin@local',
  '$2a$10$6Ox6X4Lgt8Fub/dchTNqauH/xQOdpQTLN34eyCmOof9DzmDhpnfYO',
  'ADMIN'
),
(
  'Regular',
  'User',
  'user',
  'user@local',
  '$2a$10$8.GVTJHz11j03UuwWlS50OdzQ73YNrEVKKp3rrBScxJX.ylW8QhqS',
  'USER'
);