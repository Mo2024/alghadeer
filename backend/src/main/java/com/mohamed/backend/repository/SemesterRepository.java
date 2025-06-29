package com.mohamed.backend.repository;

import com.mohamed.backend.model.Semester;
import com.mohamed.backend.model.SemesterList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    boolean existsByYearAndSemester(Integer year, SemesterList semester);
    boolean existsByActive(boolean active);
    Optional<Semester> findByActive(boolean active);
}
