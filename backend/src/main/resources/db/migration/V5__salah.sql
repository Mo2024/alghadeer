create sequence questions_sequence start with 1 increment by 1;
CREATE TABLE questions (
    id INT PRIMARY KEY,
    question TEXT,
    level VARCHAR(10) CHECK (level IN ('ONE', 'TWO', 'THREE')),
    deleted BOOLEAN DEFAULT FALSE
);


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
