ALTER TABLE student_enrollments
ADD COLUMN enrollment_status TEXT
CHECK (enrollment_status IN ('ACTIVE', 'DROPPED_EXCESS_ABSENCE'));

UPDATE student_enrollments
SET enrollment_status = 'ACTIVE'
WHERE enrollment_status IS NULL;
