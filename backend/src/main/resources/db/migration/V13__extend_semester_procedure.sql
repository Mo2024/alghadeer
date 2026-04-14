CREATE OR REPLACE PROCEDURE extendSemester(p_new_date DATE)
LANGUAGE plpgsql
AS $$
DECLARE
    v_semester_id INT;
    v_old_end_date    DATE;
	v_staff_id INT;
    r_record RECORD;
BEGIN
    -- Get active semester
    SELECT id, end_date
    INTO v_semester_id, v_old_end_date
    FROM semesters
    WHERE active = true
    LIMIT 1;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No active semester found';
    END IF;

	IF p_new_date <= v_old_end_date THEN
	    RAISE EXCEPTION 'Invalid date: new end date (%) must be greater than current end date (%)',
	        p_new_date, v_old_end_date;
	END IF;

    -- Loop through distinct class schedules
    FOR r_record IN
        SELECT DISTINCT ON (cs.id)
            cs.*
        FROM grade_class_assignment gca
        INNER JOIN class_schedule cs
            ON cs.class_id = gca.class_id
        WHERE gca.semester_id = v_semester_id
        ORDER BY cs.id
    LOOP
        RAISE NOTICE 'Processing class_schedule id: %', r_record.id;

	    SELECT staff_id
	    INTO v_staff_id
	    FROM classes
	    WHERE id = r_record.class_id
	    LIMIT 1;


		INSERT INTO sessions (id, semester_id, staff_id, date, class_id, cancelled)
		SELECT nextval('sessions_sequence'), v_semester_id, v_staff_id, session_date::date, r_record.class_id, false
		FROM GENERATE_SERIES(v_old_end_date::DATE + INTERVAL '1 DAY', p_new_date,INTERVAL '1 DAY') AS session_date
		WHERE TRIM(TO_CHAR(session_date, 'DAY')) = r_record.day_of_week;

    END LOOP;

	UPDATE semesters set end_date = p_new_date where id = v_semester_id;


END;
$$;