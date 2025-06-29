package com.mohamed.backend.repository;

import com.mohamed.backend.model.SemesterEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SemesterEnrollmentRepository extends JpaRepository<SemesterEnrollment, Integer> {
    boolean existsByStudentIdAndSemesterId(Integer studentId, Integer semesterId);
}
