package com.mohamed.backend.semesters.semesterEnrollments;

import com.mohamed.backend.semesters.dto.SemesterEnrollmentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterEnrollmentRepository extends JpaRepository<SemesterEnrollment, Integer> {
    boolean existsByStudentIdAndSemesterId(Integer studentId, Integer semesterId);

    List<SemesterEnrollmentView> findAllBySemesterId(int semesterId);
}
