-- Insert default role if it doesn't exist
INSERT INTO role (name) 
SELECT 'ROLE_USER' FROM dual 
WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'ROLE_USER');

INSERT INTO role (name) 
SELECT 'ROLE_ADMIN' FROM dual 
WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'ROLE_ADMIN');
