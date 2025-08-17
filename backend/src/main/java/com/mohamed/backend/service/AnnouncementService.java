package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.model.announcement.Announcement;
import com.mohamed.backend.model.announcement.AnnouncementTarget;
import com.mohamed.backend.repository.announcement.AnnouncementRepository;
import com.mohamed.backend.repository.announcement.AnnouncementTargetRepository;
import com.mohamed.backend.utils.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementTargetRepository announcementTargetRepository;
    private final Logger logger;

    public void createInternalAnnouncement(Announcement announcement, AnnouncementTarget announcementTarget) throws JsonProcessingException {
        log.info("Calling [announcementRepository].[save]");
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        log.info("[announcementRepository].[save] called successfully");

        logger.logJsonObject("Announcement: \n", savedAnnouncement);

        announcementTarget.setAnnouncement(savedAnnouncement);

        log.info("Calling [announcementTargetRepository].[save]");
        announcementTargetRepository.save(announcementTarget);
        log.info("[announcementTargetRepository].[save] called successfully");

        logger.logJsonObject("Announcement Target: \n", announcementTarget);
    }


}
