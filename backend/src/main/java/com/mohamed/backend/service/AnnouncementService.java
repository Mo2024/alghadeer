package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.dto.semester.AnnouncementView;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.announcement.Announcement;
import com.mohamed.backend.model.announcement.AnnouncementTarget;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.repository.announcement.AnnouncementRepository;
import com.mohamed.backend.repository.announcement.AnnouncementTargetRepository;
import com.mohamed.backend.repository.classinfo.ClassRepository;
import com.mohamed.backend.repository.semester.SemesterRepository;
import com.mohamed.backend.utils.Logger;
import com.mohamed.backend.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementTargetRepository announcementTargetRepository;
    private final SemesterRepository semesterRepository;
    private final ClassRepository classRepository;
    private final Logger logger;

    @Transactional
    public void createInternalAnnouncement(Announcement announcement, AnnouncementTarget announcementTarget) throws JsonProcessingException {
        log.info("Calling [announcementRepository].[save]");
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        log.info("[announcementRepository].[save] called successfully");

        logger.logJsonObject("Announcement:\n{}", savedAnnouncement);

        announcementTarget.setAnnouncement(savedAnnouncement);

        log.info("Calling [announcementTargetRepository].[save]");
        announcementTargetRepository.save(announcementTarget);
        log.info("[announcementTargetRepository].[save] called successfully");

        logger.logJsonObject("Announcement Target:\n{}", announcementTarget);
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR')")
    public Response createAnnouncement(Announcement announcement) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", announcement);

        log.info("Calling [semesterRepository].[findByActive]");
        Semester activeSemester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });
        log.info("[semesterRepository].[findByActive] called successfully");

        announcement.setSemester(activeSemester);
        announcement.setCancelled(false);
        announcement.setAssignment(null);

        if (announcement.getAnnouncementType() == null) {
            log.error("Announcement type must not be null");
            throw new HandledRejection("نوع الإعلان مطلوب");
        }

        if (announcement.getContent() == null || announcement.getContent().trim().isEmpty() || !ValidationUtils.isArabic(announcement.getContent())) {
            log.error("Announcement content must not be empty");
            throw new HandledRejection("محتوى الإعلان مطلوب");
        }

        if (announcement.getStartDate() == null) {
            log.error("Announcement start date must not be null");
            throw new HandledRejection("تاريخ بدء الإعلان مطلوب");
        }

        if (announcement.getEndDate() == null) {
            log.error("Announcement end date must not be null");
            throw new HandledRejection("تاريخ انتهاء الإعلان مطلوب");
        }

        if (announcement.getEndDate().isBefore(announcement.getStartDate())) {
            log.error("End date cannot be before start date");
            throw new HandledRejection("تاريخ انتهاء الإعلان لا يمكن أن يكون قبل تاريخ البدء");
        }


        log.info("Calling [announcementRepository].[save]");
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        log.info("[announcementRepository].[save] called successfully");

        logger.logJsonObject("Announcement:\n{}", savedAnnouncement);

        if (!announcement.isGeneral()) {
            if (announcement.getAnnouncementTargets() == null || announcement.getAnnouncementTargets().isEmpty()) {
                log.error("No announcement targets provided");
                throw new HandledRejection("يجب توفير المستلمين للإعلان");
            }

            for (AnnouncementTarget target : announcement.getAnnouncementTargets()) {
                log.info("Calling [classRepository].[findByIdAndSemesterActiveTrue]");
                classRepository.findByIdAndSemesterActiveTrue(target.getSemesterClass().getId())
                        .orElseThrow(() -> {
                            log.error("Class ID does not exist or semester is not active:{}", target.getSemesterClass().getId());
                            return new HandledRejection("الصف غير موجود");
                        });
                log.info("[classRepository].[findByIdAndSemesterActiveTrue] called successfully");

                target.setAnnouncement(savedAnnouncement);

                log.info("Calling [announcementTargetRepository].[save]");
                announcementTargetRepository.save(target);
                log.info("[announcementTargetRepository].[save] called successfully");

                logger.logJsonObject("Announcement Target:\n{}", target);
            }
        }
        return new Response("تم إنشاء الإعلان بنجاح");
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR')")
    public Response cancelAnnouncement(Integer announcementId) {
        log.info("Request parameter (announcementID): {}", announcementId);

        log.info("Calling [announcementRepository].[findByIdAndSemesterActive]");
        Announcement announcement = announcementRepository.findByIdAndSemesterActiveTrue(announcementId)
                .orElseThrow(() -> {
                    log.error("Announcement not found");
                    return new HandledRejection("الإعلان المطلوب غير موجود");
                });
        log.info("[announcementRepository].[findByIdAndSemesterActive] called successfully");

        announcement.setCancelled(true);

        log.info("Calling [announcementRepository].[save]");
        announcementRepository.save(announcement);
        log.info("[announcementRepository].[save] called successfully");

        return new Response("تم إلغاء الإعلان بنجاح");
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR')")
    public Page<Announcement> getAnnouncementsByActiveSemester(Pageable pageable) {
        log.info("Calling [semesterRepository].[findByActive]");
        semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });
        log.info("[semesterRepository].[findByActive] called successfully");

        log.info("Calling [announcementRepository].[findBySemesterActiveTrueOrderByCreatedAtDesc]");
        Page<Announcement> announcements = announcementRepository.findBySemesterActiveTrueOrderByCreatedAtDesc(pageable);
        log.info("[announcementRepository].[findBySemesterActiveTrueOrderByCreatedAtDesc] called successfully");

        return announcements;
    }

    public List<AnnouncementView> findActiveAnnouncements(Integer studentId, Integer semesterId) {
        return announcementRepository.findActiveAnnouncements(studentId, semesterId);
    }


}
