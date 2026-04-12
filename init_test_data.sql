-- Скрипт для заполнения БД planify_db тестовыми данными
-- Запускать через: psql -U postgres -d planify_db -f init_test_data.sql

-- Сначала очищаем старые данные (если есть)
DELETE FROM meeting_participants;
DELETE FROM meetings;
DELETE FROM profiles WHERE userId IN (SELECT id FROM users);
DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users);
DELETE FROM users;
DELETE FROM roles;
DELETE FROM authorities;

-- Вставляем роли
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER') ON CONFLICT (id) DO NOTHING;

-- Вставляем полномочия
INSERT INTO authorities (id, permission) VALUES (1, 'READ_PRIVILEGE') ON CONFLICT (id) DO NOTHING;
INSERT INTO authorities (id, permission) VALUES (2, 'WRITE_PRIVILEGE') ON CONFLICT (id) DO NOTHING;
INSERT INTO authorities (id, permission) VALUES (3, 'DELETE_PRIVILEGE') ON CONFLICT (id) DO NOTHING;

-- Связываем роли с полномочиями
INSERT INTO role_authorities (role_id, authority_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO role_authorities (role_id, authority_id) VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO role_authorities (role_id, authority_id) VALUES (1, 3) ON CONFLICT DO NOTHING;
INSERT INTO role_authorities (role_id, authority_id) VALUES (2, 1) ON CONFLICT DO NOTHING;

-- Вставляем пользователей (пароль: 123, хеширован через BCrypt с rounds=10)
-- BCrypt хеш для "123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (id, username, email, password_hash, is_active, created_at, updated_at) VALUES
(1, 'admin', 'q@w.e', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
(2, 'ivanov', 'ivanov@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
(3, 'petrova', 'petrova@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
(4, 'sidorov', 'sidorov@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
(5, 'kozlova', 'kozlova@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
(6, 'smirnov', 'smirnov@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
(7, 'novikova', 'novikova@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
(8, 'volkov', 'volkov@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    username = EXCLUDED.username,
    email = EXCLUDED.email,
    is_active = true;

-- Назначаем роли
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 2), (3, 2), (4, 2), (5, 2), (6, 2), (7, 2), (8, 2)
ON CONFLICT DO NOTHING;

-- Вставляем профили
INSERT INTO profiles (userId, firstName, lastName, position, department, profileImageUrl) VALUES
(1, 'Админ', 'Главный', 'CTO', 'Руководство', 'https://dummyimage.com/512x512/ffae00/000000.png'),
(2, 'Иван', 'Иванов', 'Разработчик', 'Backend', 'https://dummyimage.com/512x512/4CAF50/ffffff.png'),
(3, 'Мария', 'Петрова', 'Дизайнер', 'UI/UX', 'https://dummyimage.com/512x512/2196F3/ffffff.png'),
(4, 'Алексей', 'Сидоров', 'Аналитик', 'Аналитика', 'https://dummyimage.com/512x512/FF9800/ffffff.png'),
(5, 'Елена', 'Козлова', 'Тестировщик', 'QA', 'https://dummyimage.com/512x512/9C27B0/ffffff.png'),
(6, 'Дмитрий', 'Смирнов', 'DevOps', 'Инфраструктура', 'https://dummyimage.com/512x512/F44336/ffffff.png'),
(7, 'Анна', 'Новикова', 'Менеджер', 'Продукт', 'https://dummyimage.com/512x512/00BCD4/ffffff.png'),
(8, 'Сергей', 'Волков', 'Разработчик', 'Frontend', 'https://dummyimage.com/512x512/3F51B5/ffffff.png')
ON CONFLICT (userId) DO UPDATE SET
    firstName = EXCLUDED.firstName,
    lastName = EXCLUDED.lastName,
    position = EXCLUDED.position,
    department = EXCLUDED.department,
    profileImageUrl = EXCLUDED.profileImageUrl;

-- Проверяем результат
SELECT 'Users:' as info;
SELECT id, username, email, is_active FROM users;

SELECT 'Profiles:' as info;
SELECT p.userId, p.firstName, p.lastName, p.position, p.department FROM profiles p;
