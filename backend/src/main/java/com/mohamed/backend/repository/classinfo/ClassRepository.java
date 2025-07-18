package com.mohamed.backend.repository.classinfo;

import com.mohamed.backend.model.classinfo.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository  extends JpaRepository<Class, Integer> {
    @Query(value = """
    SELECT COUNT(*) = 1
    FROM student_class
    WHERE student_id = :studentId AND class_id = :classId;
""", nativeQuery = true)
    boolean isStudentInClass(@Param("studentId") Integer studentId, @Param("classId") Integer classId);

    @Query(value = """
    SELECT COUNT(*) > 0
    FROM attendance
    WHERE student_id = :studentId AND session_id = :sessionId;
""", nativeQuery = true)
    boolean isDuplicateAttendance(@Param("studentId") Integer studentId, @Param("sessionId") Integer sessionId);

    @Query(value = """
    SELECT COUNT(*) > 0
    FROM classes
    WHERE staff_id = :staffId AND id = :classId
""", nativeQuery = true)
    boolean isAuthorizedToTakeAttendanceForClass(
            @Param("staffId") Integer staffId,
            @Param("classId") Integer classId
    );

}
