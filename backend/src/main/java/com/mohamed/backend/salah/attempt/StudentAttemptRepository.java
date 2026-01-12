package com.mohamed.backend.salah.attempt;

import com.mohamed.backend.salah.attempt.dto.SalahAttemptView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAttemptRepository extends JpaRepository<StudentAttempt, Integer> {

    @Query(value = "SELECT DISTINCT ON ((elem ->> 'subjectId')::int) \n" +
            "    ssa.id,\n" +
            "    (elem ->> 'subjectId')::int AS subject_id,\n" +
            "    ssa.attempt_date_time AS latest_attempt,\n" +
            "    ssa.completed,\n" +
            "    (elem ->> 'passed')::boolean AS passed,\n" +
            "    s.name AS subjectName,\n" +
            "    ssa.level AS level\n" +
            "FROM student_salah_attempt ssa\n" +
            "CROSS JOIN LATERAL jsonb_array_elements(ssa.subjects) AS elem\n" +
            "INNER JOIN subjects s on s.id = (elem ->> 'subjectId')::int\n" +
            "INNER JOIN student_level sl ON sl.id = ssa.student_level_id \n" +
            "WHERE (elem ->> 'subjectId')::int IN (:subjectsId) AND sl.student_id = :studentId \n" +
            "ORDER BY subject_id, ssa.attempt_date_time DESC;", nativeQuery = true)
    List<SalahAttemptView> getLatestStudentAttempts(Integer studentId, List<Integer> subjectsId);
}
