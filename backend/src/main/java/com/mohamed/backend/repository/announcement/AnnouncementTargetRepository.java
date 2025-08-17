package com.mohamed.backend.repository.announcement;

import com.mohamed.backend.model.announcement.AnnouncementTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementTargetRepository extends JpaRepository<AnnouncementTarget, Integer> {
}
