INSERT INTO roles (id, name, version) VALUES ('e0a7f141-86a0-4a81-93c6-30238383a8b4', 'ROLE_ADMIN', 0);
INSERT INTO roles (id, name, version) VALUES ('9f0b1a03-9e48-43d7-8d13-67123c53c441', 'ROLE_CUSTOMER', 0);

INSERT INTO customers (id, name, surname, credit_limit, used_credit_limit, version)
VALUES ('b4a66a3d-423c-411a-8e2b-2856f6a9c394', 'User', 'Yilmaz', 1000000.0, 0.0, 0)
    ON CONFLICT (id) DO NOTHING;


INSERT INTO users (id, username, password, customer_id, version)
VALUES ('c4b8b6e3-82a1-4d1e-92d5-59b369f6e6f1', 'admin', '$2a$10$FP0YACQOdVhEO2CDvlxdsOAwy/pjkJXqRcAjB1MZGE.SNGES4GqE2', NULL, 0)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, password, customer_id, version)
VALUES ('e7f3d9b0-9f22-4217-a06f-12c8b8a9c402', 'user', '$2a$10$FP0YACQOdVhEO2CDvlxdsOAwy/pjkJXqRcAjB1MZGE.SNGES4GqE2', 'b4a66a3d-423c-411a-8e2b-2856f6a9c394',0)
    ON CONFLICT (id) DO NOTHING;


INSERT INTO user_roles (user_id, role_id)
VALUES ('c4b8b6e3-82a1-4d1e-92d5-59b369f6e6f1', 'e0a7f141-86a0-4a81-93c6-30238383a8b4')
    ON CONFLICT ON CONSTRAINT user_roles_pkey DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES ('e7f3d9b0-9f22-4217-a06f-12c8b8a9c402', '9f0b1a03-9e48-43d7-8d13-67123c53c441')
    ON CONFLICT ON CONSTRAINT user_roles_pkey DO NOTHING;