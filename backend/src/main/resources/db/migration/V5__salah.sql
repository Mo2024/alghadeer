create sequence subjects_sequence start with 1 increment by 1;
-- Create the 'subjects' table
CREATE TABLE subjects (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

create sequence subject_area_sequence start with 1 increment by 1;
-- Create the 'subject_area' table
CREATE TABLE subject_area (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    subject_id INT NOT NULL,
    CONSTRAINT fk_subject
        FOREIGN KEY (subject_id)
        REFERENCES subjects(id)
        ON DELETE CASCADE
);

create sequence questions_sequence start with 1 increment by 1;
CREATE TABLE questions (
    id INT PRIMARY KEY,
    question TEXT,
    level VARCHAR(10) CHECK (level IN ('ONE', 'TWO', 'THREE')),
    sequence INT,
    is_pillar BOOLEAN,
    deleted BOOLEAN DEFAULT FALSE,
    area_id INT NOT NULL,
    CONSTRAINT fk_area
        FOREIGN KEY (area_id)
        REFERENCES subject_area(id)
        ON DELETE CASCADE
);

create sequence student_salah_attempt_sequence start with 1 increment by 1;

CREATE TABLE student_salah_attempt (
     id INT PRIMARY KEY,
     question_id INT NOT NULL,
     student_id INT NOT NULL,
     grade INT CHECK (grade IN (0,1,2,3)),
     CONSTRAINT fk_question
         FOREIGN KEY (question_id)
         REFERENCES questions(id)
         ON DELETE CASCADE,
     CONSTRAINT fk_stwudent
         FOREIGN KEY (student_id)
         REFERENCES students(id)
         ON DELETE CASCADE
 );


CREATE OR REPLACE PROCEDURE update_question_sequence(
    IN p_seq INT,
    IN p_is_increment BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF p_is_increment THEN
        UPDATE questions
        SET sequence = sequence + 1
        WHERE sequence >= p_seq and deleted = FALSE;
    ELSE
        UPDATE questions
        SET sequence = sequence - 1
        WHERE sequence > p_seq and deleted = FALSE;
    END IF;
END;
 $$;
