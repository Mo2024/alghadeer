package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamed.backend.dto.*;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.classinfo.ClassSchedule;
import com.mohamed.backend.model.classinfo.Session;
import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.model.topics.SubTopic;
import com.mohamed.backend.repository.classinfo.ClassRepository;
import com.mohamed.backend.repository.classinfo.SessionRepository;
import com.mohamed.backend.repository.semester.SemesterRepository;
import com.mohamed.backend.repository.topic.SubTopicRepository;
import com.mohamed.backend.repository.user.StaffRepository;
import com.mohamed.backend.utils.Logger;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private SubTopicRepository subTopicRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Logger logger;

    @Transactional
    public void createSessions(Class class_) {
        for (LocalDate startDate = class_.getSemester().getStartDate();
             !startDate.isAfter(class_.getSemester().getEndDate());
             startDate = startDate.plusDays(1)) {

            for (ClassSchedule classSchedule : class_.getClassSchedules()) {

                // Below check if the dayOfWeek from the outer loop is equal to dayOfWeek of class schedule
                if (startDate.getDayOfWeek().name().equals(classSchedule.getDayOfWeek().toString())) {
                    Session session = Session.builder()
                            .date(startDate)
                            .subTopics(null)
                            .staff(class_.getStaff())
                            .semester(class_.getSemester())
                            .semesterClass(class_)
                            .cancelled(false)
                            .build();
                    log.info("Calling [sessionRepository].[save]");
                    sessionRepository.save(session);
                    log.info("[sessionRepository].[save] called successfully");

                    log.info("Session created successfully on {}", startDate);
                }

            }

        }
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    @Transactional
    public Response cancelSessionsBySessionIds(List<Integer> sessionIds) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", sessionIds);

        log.info("Calling [sessionRepository].[findByIdIn]");
        List<Session> sessions = sessionRepository.findByIdIn(sessionIds);
        log.info("[sessionRepository].[findByIdIn] called successfully");

        for (Session session : sessions) {
            log.info("Calling [sessionRepository].[isAuthorizedToTakeAttendanceForSession]");
            boolean isAssignedToSession = sessionRepository.isAuthorizedToTakeAttendanceForSession(staffService.getStaffId(), session.getId());
            log.info("[sessionRepository].[isAuthorizedToTakeAttendanceForSession] called successfully");

            log.info("Calling [classRepository].[isAuthorizedToTakeAttendanceForClass]");
            boolean isAssignedToClass = classRepository.isAuthorizedToTakeAttendanceForClass(staffService.getStaffId(), session.getSemesterClass().getId());
            log.info("[classRepository].[isAuthorizedToTakeAttendanceForClass] called successfully");

            log.info("Calling [staffRepository].[isInstructorOnly]");
            boolean isInstructorOnly = staffRepository.isInstructorOnly(staffService.getStaffId());
            log.info("[staffRepository].[isInstructorOnly] called successfully");

            // idk why i put the above query pls revise and revise this logic tbh I think to make sure admins/staff don't get validated?
            if ((isAssignedToClass || isAssignedToSession) && isInstructorOnly) {
                log.error("Staff instructor is not assigned to this class/session");
                throw new HandledRejection("المُدرّس غير مُعيّن في هذا الصف لإلغاء الحصة");
            }

            if (session.getCancelled()) {
                logger.logJsonObjectError("Staff tried to cancel a cancelled session:\n{}", session);
                throw new HandledRejection("لا يمكن إلغاء حصة تم إلغاؤها مسبقًا");
            }

            if (!session.getSemester().getActive()) {
                logger.logJsonObjectError("Staff tried to cancel a session from a closed semester:\n{}", session);
                throw new HandledRejection("لا يمكن إلغاء حصة من فصل منتهٍ");
            }

            session.setCancelled(true);
        }

        log.info("Calling [sessionRepository].[saveAll]");
        sessionRepository.saveAll(sessions);
        log.info("[sessionRepository].[saveAll] called successfully");

        return new Response("تم إلغاء الحصص بنجاح");
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    @Transactional
    public Response cancelSessionsByDates(List<LocalDate> dates) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", dates);

        log.info("Calling [sessionRepository].[findByDateIn]");
        List<Session> sessions = sessionRepository.findByDateIn(dates);
        log.info("[sessionRepository].[findByDateIn] called successfully");

        for (Session session : sessions) {
            log.info("Calling [sessionRepository].[isAuthorizedToTakeAttendanceForSession]");
            boolean isAssignedToSession = sessionRepository.isAuthorizedToTakeAttendanceForSession(staffService.getStaffId(), session.getId());
            log.info("[sessionRepository].[isAuthorizedToTakeAttendanceForSession] called successfully");

            log.info("Calling [classRepository].[isAuthorizedToTakeAttendanceForClass]");
            boolean isAssignedToClass = classRepository.isAuthorizedToTakeAttendanceForClass(staffService.getStaffId(), session.getSemesterClass().getId());
            log.info("[classRepository].[isAuthorizedToTakeAttendanceForClass] called successfully");

            log.info("Calling [staffRepository].[isInstructorOnly]");
            boolean isInstructorOnly = staffRepository.isInstructorOnly(staffService.getStaffId());
            log.info("[staffRepository].[isInstructorOnly] called successfully");

            // idk why i put the above query pls revise and revise this logic tbh I think to make sure admins/staff don't get validated?
            if ((isAssignedToClass || isAssignedToSession) && isInstructorOnly) {
                log.error("Staff instructor is not assigned to this class/session");
                throw new HandledRejection("المُدرّس غير مُعيّن في هذا الصف لإلغاء الحصة");
            }

            if (session.getCancelled()) {
                logger.logJsonObjectError("Staff tried to cancel a cancelled session:\n{}", session);
                throw new HandledRejection("لا يمكن إلغاء حصة تم إلغاؤها مسبقًا");
            }

            if (!session.getSemester().getActive()) {
                logger.logJsonObjectError("Staff tried to cancel a session from a closed semester:\n{}", session);
                throw new HandledRejection("لا يمكن إلغاء حصة من فصل منتهٍ");
            }

            session.setCancelled(true);
        }

        log.info("Calling [sessionRepository].[saveAll]");
        sessionRepository.saveAll(sessions);
        log.info("[sessionRepository].[saveAll] called successfully");

        return new Response("تم إلغاء الحصص بنجاح");
    }


    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    @Transactional
    public Response changeSubTopic(AddSubTopicDto addSubTopicDto) throws JsonProcessingException {

        log.info("Calling [sessionRepository].[findById]");
        Session session = sessionRepository.findById(addSubTopicDto.getSessionId())
                .orElseThrow(() -> {
                    log.error("Session not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[sessionRepository].[findById] called successfully");

        List<SubTopic> subTopics = new ArrayList<>();
        log.info("Calling [subTopicRepository].[findById] for multiple IDs");
        for (Integer id : addSubTopicDto.getSubTopicsId()) {
            SubTopic subTopic = subTopicRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Sub topic not found");
                        return new HandledRejection("الرجاء التحقق من وجود الموضوع الفرعي");
                    });
            subTopics.add(subTopic);
        }
        log.info("[subTopicRepository].[findById] called successfully for all IDs");


        log.info("Calling [sessionRepository].[isAuthorizedToTakeAttendanceForSession]");
        boolean isAssignedToSession = sessionRepository.isAuthorizedToTakeAttendanceForSession(staffService.getStaffId(), session.getId());
        log.info("[sessionRepository].[isAuthorizedToTakeAttendanceForSession] called successfully");

        log.info("Calling [classRepository].[isAuthorizedToTakeAttendanceForClass]");
        boolean isAssignedToClass = classRepository.isAuthorizedToTakeAttendanceForClass(staffService.getStaffId(), session.getSemesterClass().getId());
        log.info("[classRepository].[isAuthorizedToTakeAttendanceForClass] called successfully");

        log.info("Calling [staffRepository].[isInstructorOnly]");
        boolean isInstructorOnly = staffRepository.isInstructorOnly(staffService.getStaffId());
        log.info("[staffRepository].[isInstructorOnly] called successfully");

        // idk why i put the above query pls revise and revise this logic tbh I think to make sure admins/staff don't get validated?
        if ((isAssignedToClass || isAssignedToSession) && isInstructorOnly) {
            log.error("Staff instructor is not assigned to this class/session");
            throw new HandledRejection("المُدرّس غير مُعيّن في هذا الصف لإلغاء الحصة");
        }

        if (session.getCancelled()) {
            logger.logJsonObjectError("Staff tried to change a sub-topic of a cancelled session:\n{}", session);

            throw new HandledRejection("لا يمكن تغيير الموضوع الفرعي لحصة تم إلغاؤها مسبقًا");
        }

        if (!session.getSemester().getActive()) {
            logger.logJsonObjectError("Staff tried to change a sub-topic of a closed semester:\n{}", session);
            throw new HandledRejection("لا يمكن تغيير الموضوع الفرعي لحصة في فصل دراسي مغلق");
        }

        session.setSubTopics(subTopics);

        log.info("Calling [subTopicRepository].[save]");
        sessionRepository.save(session);
        log.info("[subTopicRepository].[save] called successfully");

        return new Response("تم تعيين الموضوع الفرعي للحصة بنجاح");
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Page<SessionViewExtends> getUpcomingSessions(Pageable pageable) throws JsonProcessingException {
        log.info("Calling [semesterRepository].[findByActive]");
        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });
        log.info("[semesterRepository].[findByActive] called successfully");

        logger.logJsonObject("Semester Details:\n{}", semester);

        log.info("Calling [sessionRepository].[findAllByStaffIdAndDateGreaterThanEqualAndCancelledFalseOrderByDateAsc]");
        Page<SessionViewExtends> upcomingSessions = sessionRepository.findAllByStaffIdAndDateGreaterThanEqualAndCancelledFalseOrderByDateAsc(
                staffService.getStaffId(),
                LocalDate.now(),
                pageable
        );
        log.info("[sessionRepository].[findAllByStaffIdAndDateGreaterThanEqualAndCancelledFalseOrderByDateAsc] called successfully");

        logger.logJsonObject("Upcoming sessions:\n{}", upcomingSessions);

        return upcomingSessions;
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<SessionView> getSessionsByActiveSemester() throws JsonProcessingException {

        log.info("Calling [semesterRepository].[findByActive]");
        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });
        log.info("[semesterRepository].[findByActive] called successfully");

        logger.logJsonObject("Semester Details:\n{}", semester);

        log.info("Calling [sessionRepository].[findBySemesterActiveTrueAndCancelledFalseOrderByDateAsc]");
        List<SessionView> sessions = sessionRepository.findBySemesterActiveTrueAndCancelledFalseOrderByDateAsc();
        log.info("[sessionRepository].[findBySemesterActiveTrueAndCancelledFalseOrderByDateAsc] called successfully");

        logger.logJsonObject("Sessions of active semester:\n{}", sessions);

        return sessions;
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<LocalDate> getDatesOfSessions() throws JsonProcessingException {

        log.info("Calling [semesterRepository].[findByActive]");
        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });
        log.info("[semesterRepository].[findByActive] called successfully");

        logger.logJsonObject("Semester Details:\n{}", semester);

        log.info("Calling [sessionRepository].[findDistinctDatesBySemesterActiveTrueAndCancelledFalseOrderByDateAsc]");
        List<LocalDate> dates = sessionRepository.findDistinctDatesBySemesterActiveTrueAndCancelledFalseOrderByDateAsc();
        log.info("[sessionRepository].[findDistinctDatesBySemesterActiveTrueAndCancelledFalseOrderByDateAsc] called successfully");

        logger.logJsonObject("Dates of sessions:\n{}", dates);

        return dates;
    }
}
