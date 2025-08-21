package com.mohamed.backend.repository.announcement;

import com.mohamed.backend.dto.semester.AnnouncementView;
import com.mohamed.backend.model.announcement.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    Optional<Announcement> findByIdAndSemesterActiveTrue(Integer announcementId);

    Page<Announcement> findBySemesterActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = """
            WITH combined AS (
                SELECT a.*
                FROM student_class sc
                INNER JOIN announcement_targets atr ON atr.class_id = sc.class_id
                INNER JOIN announcements a ON a.id = atr.announcement_id
                WHERE sc.student_id = :studentId 
                  AND a.semester_id = :semesterId 
                  AND a.is_cancelled = false
            
                UNION ALL
            
                SELECT * 
                FROM announcements 
                WHERE semester_id = :semesterId 
                  AND is_general = true 
                  AND is_cancelled = false
            )
            SELECT DISTINCT ON (id) *
            FROM combined
            WHERE start_date_time < NOW() AND end_date_time > NOW()
            ORDER BY id, created_at DESC
            """, nativeQuery = true)
    List<AnnouncementView> findActiveAnnouncements(
            @Param("studentId") Integer studentId,
            @Param("semesterId") Integer semesterId
    );
}
