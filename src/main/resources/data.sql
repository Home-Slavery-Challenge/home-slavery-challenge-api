-- =====================
-- ROLES
-- =====================
INSERT INTO role (id, name, created_at, updated_at)
VALUES
  (1, 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'USER',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================
-- USERS
-- =====================
INSERT INTO users (id, username, password, enabled, email, created_at, updated_at)
VALUES
  (1, 'alice',   'password123', TRUE,  'alice@example.com',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'bob',     'password123', TRUE,  'bob@example.com',     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 'charlie', 'password123', FALSE, 'charlie@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===================================================
-- Association USERS ↔ ROLES
-- ===================================================
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);  -- alice ADMIN
INSERT INTO user_role (user_id, role_id) VALUES (1, 2);  -- alice USER
INSERT INTO user_role (user_id, role_id) VALUES (2, 2);  -- bob USER
INSERT INTO user_role (user_id, role_id) VALUES (3, 2);  -- charlie USER

-- ===================================================
-- Friendship de base pour les tests
-- ===================================================
-- Relation PENDING : bob (id=2) → alice (id=1)
-- is_checked = FALSE pour tester markAsChecked()
INSERT INTO friendship (id, created_at, is_checked, status, updated_at, receiver_id, requester_id)
VALUES
  (1, CURRENT_TIMESTAMP, FALSE, 'PENDING', CURRENT_TIMESTAMP, 1, 2);
