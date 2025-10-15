package com.mohamed.backend.salah.questions.subjects;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectAreaRepository  extends JpaRepository<SubjectArea, Integer> {
    Optional<SubjectArea> findByIdAndSubjectId(Integer id, Integer id1);
}
