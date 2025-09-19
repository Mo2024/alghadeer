package com.mohamed.backend.classes.gradeClassAssignments;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeClassAssignmentRepository extends JpaRepository<GradeClassAssignment, Integer> {

    GradeClassAssignment findBySemesterIdAndGrade(Integer semesterId, Grade grade);
}
