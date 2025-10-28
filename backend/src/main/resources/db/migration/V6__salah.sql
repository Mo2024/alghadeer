ALTER TABLE questions
ADD COLUMN subject_id INTEGER NOT NULL,
ADD CONSTRAINT fk_subject
    FOREIGN KEY (subject_id)
    REFERENCES subjects(id);

ALTER TABLE questions
DROP CONSTRAINT IF EXISTS questions_level_check;

ALTER TABLE questions
ADD CONSTRAINT questions_level_check
CHECK (level IN ('ONE', 'TWO', 'THREE', 'FOUR'));