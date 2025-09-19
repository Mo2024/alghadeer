package com.mohamed.backend.announcements.annnouncementTarget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementTargetRepository extends JpaRepository<AnnouncementTarget, Integer> {
}
