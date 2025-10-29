DROP PROCEDURE IF EXISTS update_question_sequence(INTEGER, BOOLEAN);

CREATE OR REPLACE PROCEDURE update_question_sequence(
    IN p_seq INT,
    IN p_is_increment BOOLEAN,
    IN p_level VARCHAR(10)
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF p_is_increment THEN
        UPDATE questions
        SET sequence = sequence + 1
        WHERE sequence >= p_seq and deleted = FALSE and level = p_level;
    ELSE
        UPDATE questions
        SET sequence = sequence - 1
        WHERE sequence > p_seq and deleted = FALSE and level = p_level;
    END IF;
END;
 $$;