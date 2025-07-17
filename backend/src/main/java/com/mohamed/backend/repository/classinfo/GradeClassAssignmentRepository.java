package com.mohamed.backend.repository.classinfo;

import com.mohamed.backend.model.classinfo.GradeClassAssignment;
import com.mohamed.backend.model.enums.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeClassAssignmentRepository extends JpaRepository<GradeClassAssignment, Integer> {

    GradeClassAssignment findBySemesterIdAndGrade(Integer semesterId, Grade grade);
}
