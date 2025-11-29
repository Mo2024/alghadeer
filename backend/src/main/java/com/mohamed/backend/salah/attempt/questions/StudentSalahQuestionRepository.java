package com.mohamed.backend.salah.attempt.questions;

import com.mohamed.backend.salah.questions.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentSalahQuestionRepository  extends JpaRepository<StudentSalahQuestion, Integer> {

    @Query("""
    SELECT
        NULL AS id,
        NULL AS grade,
        NULL AS evaluation,
        q AS question,
        sa as studentSalahAttempt
    
    FROM Question q
    LEFT JOIN StudentAttempt sa ON sa.id = :attemptId
    WHERE q.subject.id IN (:subjectsId)
      AND q.deleted = false
      AND q.level = :level
    ORDER BY q.subject.id, q.sequence ASC
""")
    List<StudentSalahQuestionView> getFreshStudentSalahQuestions(
            List<Integer> subjectsId,
            Level level,
            Integer attemptId
    );

    @Query("""
    SELECT COUNT(q)
    FROM Question q
    LEFT JOIN StudentAttempt sa ON sa.id = :attemptId
    WHERE q.subject.id IN (:subjectsId)
      AND q.deleted = false
      AND q.level = :level
""")
    int getFreshStudentSalahQuestionsCount(
            List<Integer> subjectsId,
            Level level,
            Integer attemptId
    );

    List<StudentSalahQuestionView> findByStudentSalahAttemptId(int attemptId);

    int countByStudentSalahAttemptId(int attemptId);
}

