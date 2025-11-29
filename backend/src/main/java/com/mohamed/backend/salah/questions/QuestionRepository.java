package com.mohamed.backend.salah.questions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findAllByLevelAndDeletedFalseOrderBySequenceAsc(Level level);

    Optional<Question> findByIdAndDeletedFalse(Integer id);

    Integer countByLevelAndDeletedFalse(Level level);

    Boolean existsBySubject_Id(Integer subjectId);
}
