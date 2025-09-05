INSERT INTO jobs(name) VALUES
  ('FRONTEND'),
  ('BACKEND'),
  ('IOS'),
  ('ANDROID'),
  ('DATA')
ON DUPLICATE KEY UPDATE name = VALUES(name);


