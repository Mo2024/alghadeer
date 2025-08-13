package com.mohamed.backend.repository.classinfo.assignment;

import com.mohamed.backend.model.classinfo.assignment.StudentsAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentsAssignmentRepository extends JpaRepository<StudentsAssignment, Integer> {

    @Modifying
    @Query(value = """
            INSERT INTO students_assignments (id, grade, submission_date, assignment_id, student_id, assignment_done)
            SELECT nextval('students_assignments_sequence'), null, null, :assignmentId, sc.student_id, false
            FROM student_class sc
            WHERE sc.class_id = :classId
            """, nativeQuery = true)
    int bulkCreateStudentAssignment(@Param("assignmentId") Integer assignmentId, @Param("classId") Integer classId);

}
