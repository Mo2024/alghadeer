package com.mohamed.backend.repository;

import com.mohamed.backend.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
}
