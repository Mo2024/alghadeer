package com.mohamed.backend.salah.questions;

import com.mohamed.backend.salah.attempt.StudentAttempt;
import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestionView;
import com.mohamed.backend.users.students.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findAllByLevelAndDeletedFalseOrderBySequenceAsc(Level level);

    Optional<Question> findByIdAndDeletedFalse(Integer id);

    Integer countByLevelAndDeletedFalse(Level level);

}
