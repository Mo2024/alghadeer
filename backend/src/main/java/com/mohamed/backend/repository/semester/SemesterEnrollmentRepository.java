package com.mohamed.backend.repository.semester;

import com.mohamed.backend.dto.SemesterEnrollmentView;
import com.mohamed.backend.dto.StudentView;
import com.mohamed.backend.model.semester.SemesterEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterEnrollmentRepository extends JpaRepository<SemesterEnrollment, Integer> {
    boolean existsByStudentIdAndSemesterId(Integer studentId, Integer semesterId);
    List<SemesterEnrollmentView> findAllBySemesterId(int semesterId);
}
