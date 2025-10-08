create sequence questions_sequence start with 1 increment by 1;
CREATE TABLE questions (
    id INT PRIMARY KEY,
    question TEXT,
    level VARCHAR(10) CHECK (level IN ('ONE', 'TWO', 'THREE')),
    deleted BOOLEAN DEFAULT FALSE
);
