package com.mohamed.backend.salah.questions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findAllByLevelAndDeletedFalse(Level level);
    
    Page<Question> findAllByDeletedFalse(Pageable pageable);

    Optional<Question> findByIdAndDeletedFalse(Integer id);

    Integer countByLevelAndDeletedFalse(Level level);
}
