package com.mohamed.backend.semesters;

import com.mohamed.backend.semesters.dto.SemesterView;
import com.mohamed.backend.semesters.dto.SemesterView2;
import com.mohamed.backend.semesters.dto.StudentExportDto;
import com.mohamed.backend.semesters.semesterEnrollments.SemesterEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    @Query("SELECT COUNT(s) > 0 FROM Semester s WHERE YEAR(s.startDate) = :year AND s.semester = :semester")
    boolean existsByYearAndSemester(@Param("year") Integer year, @Param("semester") SemesterList semester);

    Page<SemesterView> findAllByOrderByIdDesc(Pageable pageable);

    @Query("""
                SELECT COUNT(s) > 0
                FROM Semester s
                WHERE
                    (:startDate <= s.endDate AND :endDate >= s.startDate)
            """)
    boolean existsOverlappingSemester(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
        select s2.name as name,
               s2.cpr,
               s2.telephone_1 as telephone1, 
               s2.telephone_2 as telephone2, 
               c.name as className,
               se.enrollment_date as enrollmentDate
        from semesters s
        inner join classes c on c.semester_id = s.id
        inner join student_class sc on sc.class_id = c.id
        inner join students s2 on s2.id = sc.student_id
        inner join student_enrollments se on se.student_id = s2.id and se.semester_id = s.id
        where s.id = :semesterId
        order by enrollmentDate asc
        """, nativeQuery = true)
    List<StudentExportDto> getEnrolledStudentsTelephone(@Param("semesterId") Integer semesterId);

    @Query(value = "SELECT s.id AS id, s.name AS name FROM semesters s ORDER BY s.start_date DESC LIMIT 3", nativeQuery = true)
    List<SemesterView2> getLatestThreeSemesters();

    boolean existsByActive(boolean active);

    Optional<Semester> findByActive(boolean active);

}
