package com.mohamed.backend.repository.semester;

import com.mohamed.backend.dto.semester.SemesterView;
import com.mohamed.backend.dto.semester.StudentExportDto;
import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.model.enums.SemesterList;
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
               s2.telephone_1 as telephone1, 
               s2.telephone_2 as telephone2, 
               c.name as className
        from semesters s
        inner join classes c on c.semester_id = s.id
        inner join student_class sc on sc.class_id = c.id
        inner join students s2 on s2.id = sc.student_id
        where s.active = true
        order by c.name asc
        """, nativeQuery = true)
    List<StudentExportDto> getEnrolledStudentsTelephone();

    boolean existsByActive(boolean active);

    Optional<Semester> findByActive(boolean active);
}
