package com.mohamed.backend.salah.questions.subjects;

import com.mohamed.backend.salah.questions.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository  extends JpaRepository<Subject, Integer> {
    @Query(value = "SELECT COUNT(*) AS total_subjects FROM (SELECT s.id FROM questions q INNER JOIN subjects s ON s.id = q.subject_id WHERE s.id IN (:subjectsId) AND q.level = :level GROUP BY s.id) AS sub;", nativeQuery = true)
    int countOfSelectedSubjectsInLevel(@Param("subjectsId") List<Integer> subjectsId, @Param("level") String level);

    @Query(value = "SELECT s.id FROM Question q INNER JOIN q.subject s WHERE q.level = :level")
    List<Integer> subjectsIdByLevel(Level level);

}
