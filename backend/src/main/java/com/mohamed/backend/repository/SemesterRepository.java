package com.mohamed.backend.repository;

import com.mohamed.backend.model.Semester;
import com.mohamed.backend.model.SemesterList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    @Query("SELECT COUNT(s) > 0 FROM Semester s WHERE YEAR(s.startDate) = :year AND s.semester = :semester")
    boolean existsByYearAndSemester(@Param("year") Integer year, @Param("semester") SemesterList semester);

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

    boolean existsByActive(boolean active);
    Optional<Semester> findByActive(boolean active);
}
