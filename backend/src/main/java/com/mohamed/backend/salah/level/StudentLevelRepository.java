package com.mohamed.backend.salah.level;

import com.mohamed.backend.salah.questions.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentLevelRepository extends JpaRepository<StudentLevel, Integer> {
    Optional<StudentLevel> findByStudentId(Integer id);

    @Query("SELECT s.level FROM StudentLevel s WHERE s.student.id = :studentId")
    Level findLevelByStudentId(int studentId);

    StudentLevel findById(int studentLevelId);
}
