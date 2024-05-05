-- Insert statement for the admin user
INSERT INTO application_user
(email, password, first_name, family_name, phone_number, salt, login_count, account_locked, DTYPE)
VALUES
    ('admin@email.com', '$2a$10$Ykq85kVq4YD1mg8JkiG42ulMhi962IM6IOrtp.1SqOCy6zVAYuVdq', 'Ernie', 'Erna', '+430987654321', 'sussy', 0, false,'ADMIN');

INSERT INTO application_user
(email, password, first_name, family_name, phone_number, salt, login_count, account_locked, DTYPE)
VALUES
    ('user@email.com', '$2a$10$jwEjrg1mGYP3k/bC3DykpuUe5.LpwPz925mi9.GJ1jShUYFs9UbRO', 'Bert', 'Berta', '+431234567890', 'sus', 0, false,'CUSTOMER');

