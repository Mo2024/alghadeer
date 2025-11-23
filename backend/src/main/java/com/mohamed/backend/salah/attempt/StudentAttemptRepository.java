package com.mohamed.backend.salah.attempt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAttemptRepository extends JpaRepository<StudentAttempt, Integer> {

    @Query(value = "SELECT DISTINCT ON (elem::int) \n" +
            "    ssa.id,\n" +
            "    elem::int AS subject_id,\n" +
            "    ssa.attempt_date_time AS latest_attempt,\n" +
            "    ssa.completed,\n" +
            "    ssa.passed,\n" +
            "    s.name AS subjectName\n" +
            "FROM student_salah_attempt ssa\n" +
            "CROSS JOIN LATERAL jsonb_array_elements(ssa.subjects) AS elem\n" +
            "INNER JOIN subjects s on s.id = elem::int\n" +
            "INNER JOIN student_level sl ON sl.id = ssa.student_level_id \n" +
            "WHERE elem::int IN (:subjectsId) AND sl.student_id = :studentId \n" +
            "ORDER BY subject_id, ssa.attempt_date_time DESC;", nativeQuery = true)
    List<SalahAttemptView> getLatestStudentAttempts(Integer studentId, List<Integer> subjectsId);
}
