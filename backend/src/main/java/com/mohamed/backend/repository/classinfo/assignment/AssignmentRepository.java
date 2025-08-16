package com.mohamed.backend.repository.classinfo.assignment;

import com.mohamed.backend.model.classinfo.assignment.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    @Query("SELECT a FROM Assignment a WHERE a.class_.id = :classId")
    List<Assignment> findByClassId(@Param("classId") Integer classId);
}
