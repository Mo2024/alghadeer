package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mohamed.backend.dto.AddSubTopicDto;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.dto.SessionDto;
import com.mohamed.backend.dto.SessionView;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                            .subTopic(null)
                            .staff(class_.getStaff())
                            .semester(class_.getSemester())
                            .semesterClass(class_)
                            .cancelled(false)
                            .build();

                    sessionRepository.save(session);
                    log.info("Session created successfully on {}", startDate);
                }

            }

        }
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    @Transactional
    public Response cancelSessions(List<Integer> sessionIds) throws JsonProcessingException {

        List<Session> sessions = sessionRepository.findByIdIn(sessionIds);

        for (Session session : sessions) {
            boolean isAssignedToSession = sessionRepository.isAuthorizedToTakeAttendanceForSession(staffService.getStaffId(), session.getId());
            boolean isAssignedToClass = classRepository.isAuthorizedToTakeAttendanceForClass(staffService.getStaffId(), session.getSemesterClass().getId());
            boolean isInstructorOnly = staffRepository.isInstructorOnly(staffService.getStaffId());
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

        sessionRepository.saveAll(sessions);

        return new Response("تم إلغاء الحصص بنجاح");
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    @Transactional
    public Response changeSubTopic(AddSubTopicDto addSubTopicDto) throws JsonProcessingException {


        Session session = sessionRepository.findById(addSubTopicDto.getSessionId())
                .orElseThrow(() -> {
                    log.error("Session not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });

        SubTopic subTopic = subTopicRepository.findById(addSubTopicDto.getSubTopicId())
                .orElseThrow(() -> {
                    log.error("Sub topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الفرعي");
                });

        boolean isAssignedToSession = sessionRepository.isAuthorizedToTakeAttendanceForSession(staffService.getStaffId(), session.getId());
        boolean isAssignedToClass = classRepository.isAuthorizedToTakeAttendanceForClass(staffService.getStaffId(), session.getSemesterClass().getId());
        boolean isInstructorOnly = staffRepository.isInstructorOnly(staffService.getStaffId());
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

        session.setSubTopic(subTopic);

        sessionRepository.save(session);

        return new Response("تم تعيين الموضوع الفرعي للحصة بنجاح");
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<SessionView> getUpcomingSessions() throws JsonProcessingException {
        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });

        logger.logJsonObject("Semester Details:\n{}", semester);

        List<SessionView> upcomingSessions = sessionRepository.findAllByStaffIdAndDateGreaterThanEqual(
                staffService.getStaffId(),
                LocalDate.now()
        );

        logger.logJsonObject("Upcoming sessions:\n{}", upcomingSessions);

        return upcomingSessions;
    }

}
