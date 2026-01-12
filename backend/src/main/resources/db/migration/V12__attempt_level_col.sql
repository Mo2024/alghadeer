ALTER TABLE student_salah_attempt
ADD COLUMN level TEXT
CHECK (level IN ('ONE', 'TWO', 'THREE', 'FOUR'));