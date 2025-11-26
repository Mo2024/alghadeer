CREATE TABLE student_level (
    id INT PRIMARY KEY,
    student_id INT,
    level TEXT CHECK (level IN ('ONE', 'TWO', 'THREE', 'FOUR')),
    CONSTRAINT fk_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
);

create sequence student_level_sequence start with 1 increment by 1;

CREATE TABLE student_salah_attempt(
    id INT PRIMARY KEY,
    student_level_id INT,
    attempt_date_time timestamp(6),
    passed BOOLEAN,
    subjects JSONB,
    comments TEXT,
    completed BOOLEAN,
    CONSTRAINT fk_student_level
        FOREIGN KEY (student_level_id)
        REFERENCES student_level(id)
);

create sequence student_salah_attempt_sequence start with 1 increment by 1;


CREATE TABLE student_salah_question (
    id INT PRIMARY KEY,
    grade INT,
    question_id INT,
    student_salah_attempt_id INT,
    evaluation TEXT CHECK (evaluation IN ('ITQAN', 'LA_BAS', 'GHAYR_MOTAMAKEN', 'YANSAA_AW_LA_YAALAM')),

    CONSTRAINT fk_question
        FOREIGN KEY (question_id)
        REFERENCES questions(id),
    CONSTRAINT fk_student_salah_attempt
        FOREIGN KEY (student_salah_attempt_id)
        REFERENCES student_salah_attempt(id)
);

create sequence student_salah_question_sequence start with 1 increment by 1;
