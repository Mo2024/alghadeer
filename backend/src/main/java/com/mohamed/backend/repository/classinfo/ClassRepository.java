package com.mohamed.backend.repository.classinfo;

import com.mohamed.backend.dto.class_.ClassView;
import com.mohamed.backend.dto.class_.ClassView2;
import com.mohamed.backend.dto.user.StudentAttendanceView;
import com.mohamed.backend.model.classinfo.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Integer> {
    @Query(value = """
                SELECT COUNT(*) = 1
                FROM student_class
                WHERE student_id = :studentId AND class_id = :classId;
            """, nativeQuery = true)
    boolean isStudentInClass(@Param("studentId") int studentId, @Param("classId") int classId);

    @Query(value = """
                SELECT COUNT(*) > 0
                FROM attendance
                WHERE student_id = :studentId AND session_id = :sessionId;
            """, nativeQuery = true)
    boolean isDuplicateAttendance(@Param("studentId") int studentId, @Param("sessionId") int sessionId);

    @Query(value = """
                SELECT COUNT(*) > 0
                FROM classes
                WHERE staff_id = :staffId AND id = :classId
            """, nativeQuery = true)
    boolean isAuthorizedToTakeAttendanceForClass(
            @Param("staffId") int staffId,
            @Param("classId") int classId
    );

    @Modifying
    @Query(value = """
                UPDATE student_class sc
                SET class_id = :classId
                FROM classes c
                WHERE sc.class_id = c.id
                  AND c.semester_id = :semesterId
                  AND sc.student_id IN (:studentsId)
            """, nativeQuery = true)
    void transferStudentsToClassInSemester(@Param("studentsId") List<Integer> studentsId,
                                           @Param("classId") int classId,
                                           @Param("semesterId") int semesterId);


    @Query(value = """
                SELECT COUNT(*)
                FROM student_class
                WHERE class_id = :classId AND student_id IN (:studentsId)
            """, nativeQuery = true)
    int countStudentsAlreadyInClass(@Param("classId") int classId, @Param("studentsId") List<Integer> studentsId);


    @Query("""
                SELECT c.id AS id, c.name AS name
                FROM Class c
                WHERE c.semester.id = :id
            """)
    List<ClassView> findAllBySemesterId(@Param("id") int id);

    @Query("""
                SELECT c
                FROM Class c
                WHERE c.staff.id = :staffId
                  AND c.semester.active = true
            """)
    List<ClassView2> findAllByStaffIdByActiveSemester(Integer staffId);

    @Query("""
                SELECT s AS student, false AS isPresent
                FROM Class c
                JOIN c.students s
                WHERE c.id = :classId
            """)
    List<StudentAttendanceView> findStudentByClassId(@Param("classId") int classId);

    List<ClassView2> findAllBySemesterActiveTrue();

    Optional<Class> findByIdAndSemesterActiveTrue(Integer classId);
}
