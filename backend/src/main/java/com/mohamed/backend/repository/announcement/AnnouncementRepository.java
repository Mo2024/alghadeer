package com.mohamed.backend.repository.announcement;

import com.mohamed.backend.model.announcement.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    Optional<Announcement> findByIdAndSemesterActiveTrue(Integer announcementId);

    Page<Announcement> findBySemesterActiveTrueOrderByCreatedAtDesc(Pageable pageable);
}
