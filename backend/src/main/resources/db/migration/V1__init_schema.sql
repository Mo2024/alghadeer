-- Insert default grade class assignments with fixed IDs
INSERT INTO grade_class_assignment (id, grade, is_default, class_id, semester_id) VALUES
(1,  'FIRST',    true, NULL, NULL),
(2,  'SECOND',   true, NULL, NULL),
(3,  'THIRD',    true, NULL, NULL),
(4,  'FOURTH',   true, NULL, NULL),
(5,  'FIFTH',    true, NULL, NULL),
(6,  'SIXTH',    true, NULL, NULL),
(7,  'SEVENTH',  true, NULL, NULL),
(8,  'EIGHTS',   true, NULL, NULL),
(9,  'NINTH',    true, NULL, NULL),
(10, 'TENTH',    true, NULL, NULL),
(11, 'ELEVENTH', true, NULL, NULL),
(12, 'TWELFTH',  true, NULL, NULL);

SELECT setval('grade_class_assignment_sequence', 13, false);
