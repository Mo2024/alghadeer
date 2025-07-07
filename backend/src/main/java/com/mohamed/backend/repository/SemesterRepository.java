package com.mohamed.backend.repository;

import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.model.enums.SemesterList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
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
