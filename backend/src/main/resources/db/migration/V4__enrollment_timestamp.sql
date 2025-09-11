ALTER TABLE student_enrollments
ALTER COLUMN enrollment_date TYPE TIMESTAMP
USING enrollment_date::timestamp;