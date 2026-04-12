-- ===== ШАГ 1: Добавляем недостающие столбцы =====
-- Добавляем is_active если ещё нет
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='is_active') THEN
        ALTER TABLE users ADD COLUMN is_active BOOLEAN DEFAULT true;
    END IF;
END $$;

-- Добавляем created_at если ещё нет
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='created_at') THEN
        ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT NOW();
    END IF;
END $$;

-- Добавляем updated_at если ещё нет
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='updated_at') THEN
        ALTER TABLE users ADD COLUMN updated_at TIMESTAMP;
    END IF;
END $$;

-- ===== ШАГ 2: Устанавливаем isActive=true для всех существующих пользователей =====
UPDATE users SET is_active = true WHERE is_active IS NULL OR is_active = false;

-- ===== ШАГ 3: Вставляем роли =====
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER') ON CONFLICT (id) DO NOTHING;

INSERT INTO authorities (id, permission) VALUES (1, 'READ_PRIVILEGE') ON CONFLICT (id) DO NOTHING;
INSERT INTO authorities (id, permission) VALUES (2, 'WRITE_PRIVILEGE') ON CONFLICT (id) DO NOTHING;
INSERT INTO authorities (id, permission) VALUES (3, 'DELETE_PRIVILEGE') ON CONFLICT (id) DO NOTHING;

INSERT INTO role_authorities (role_id, authority_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO role_authorities (role_id, authority_id) VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO role_authorities (role_id, authority_id) VALUES (1, 3) ON CONFLICT DO NOTHING;
INSERT INTO role_authorities (role_id, authority_id) VALUES (2, 1) ON CONFLICT DO NOTHING;

-- ===== ШАГ 4: Создаём тестовых пользователей =====
INSERT INTO users (username, email, password_hash, is_active, created_at) VALUES
('admin', 'q@w.e', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW())
ON CONFLICT (email) DO UPDATE SET is_active = true;

INSERT INTO users (username, email, password_hash, is_active, created_at) VALUES
('ivanov', 'ivanov@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW()),
('petrova', 'petrova@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW()),
('sidorov', 'sidorov@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW()),
('kozlova', 'kozlova@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW()),
('smirnov', 'smirnov@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW()),
('novikova', 'novikova@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW()),
('volkov', 'volkov@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW())
ON CONFLICT (email) DO UPDATE SET is_active = true;

-- ===== ШАГ 5: Назначаем роли =====
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, 1 FROM users u WHERE u.email = 'q@w.e'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, 2 FROM users u WHERE u.email != 'q@w.e'
ON CONFLICT DO NOTHING;

-- ===== ШАГ 6: Создаём профили =====
INSERT INTO profiles (userId, firstName, lastName, position, department, profileImageUrl)
SELECT u.id, 'Админ', 'Главный', 'CTO', 'Руководство', 'https://dummyimage.com/512x512/ffae00/000000.png'
FROM users u WHERE u.email = 'q@w.e'
ON CONFLICT (userId) DO UPDATE SET
    firstName = EXCLUDED.firstName,
    lastName = EXCLUDED.lastName,
    position = EXCLUDED.position,
    department = EXCLUDED.department,
    profileImageUrl = EXCLUDED.profileImageUrl;

INSERT INTO profiles (userId, firstName, lastName, position, department, profileImageUrl)
SELECT u.id, v.fn, v.ln, v.pos, v.dep, v.img FROM users u, (VALUES
    ('ivanov@company.com', 'Иван', 'Иванов', 'Разработчик', 'Backend', 'https://dummyimage.com/512x512/4CAF50/ffffff.png'),
    ('petrova@company.com', 'Мария', 'Петрова', 'Дизайнер', 'UI/UX', 'https://dummyimage.com/512x512/2196F3/ffffff.png'),
    ('sidorov@company.com', 'Алексей', 'Сидоров', 'Аналитик', 'Аналитика', 'https://dummyimage.com/512x512/FF9800/ffffff.png'),
    ('kozlova@company.com', 'Елена', 'Козлова', 'Тестировщик', 'QA', 'https://dummyimage.com/512x512/9C27B0/ffffff.png'),
    ('smirnov@company.com', 'Дмитрий', 'Смирнов', 'DevOps', 'Инфраструктура', 'https://dummyimage.com/512x512/F44336/ffffff.png'),
    ('novikova@company.com', 'Анна', 'Новикова', 'Менеджер', 'Продукт', 'https://dummyimage.com/512x512/00BCD4/ffffff.png'),
    ('volkov@company.com', 'Сергей', 'Волков', 'Разработчик', 'Frontend', 'https://dummyimage.com/512x512/3F51B5/ffffff.png')
) AS v(email, fn, ln, pos, dep, img) WHERE u.email = v.email
ON CONFLICT (userId) DO UPDATE SET
    firstName = EXCLUDED.firstName,
    lastName = EXCLUDED.lastName,
    position = EXCLUDED.position,
    department = EXCLUDED.department,
    profileImageUrl = EXCLUDED.profileImageUrl;

-- ===== ПРОВЕРКА =====
SELECT '=== Users ===' as info;
SELECT id, username, email, is_active FROM users ORDER BY id;

SELECT '=== Profiles ===' as info;
SELECT p."userId", p.firstName, p.lastName, p.position, p.department FROM profiles p ORDER BY p."userId";
