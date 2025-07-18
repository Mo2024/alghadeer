package com.mohamed.backend.service;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.classinfo.ClassSchedule;
import com.mohamed.backend.model.classinfo.Session;
import com.mohamed.backend.repository.classinfo.ClassRepository;
import com.mohamed.backend.repository.classinfo.SessionRepository;
import com.mohamed.backend.repository.user.StaffRepository;
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

    @Transactional
    public void createSessions(Class class_) {
        log.info("executing method [SessionService].[createSessions]");
        for (LocalDate startDate = class_.getSemester().getStartDate();
             !startDate.isAfter(class_.getSemester().getEndDate());
             startDate = startDate.plusDays(1))
        {

            for (ClassSchedule classSchedule : class_.getClassSchedules()){

                // Below check if the dayOfWeek from the outer loop is equal to dayOfWeek of class schedule
                if (startDate.getDayOfWeek().name().equals(classSchedule.getDayOfWeek().toString())) {
                    Session session = Session.builder()
                            .date(startDate)
                            .subTopic(null)
                            .staff(class_.getStaff())
                            .semester(class_.getSemester())
                            .class_(class_)
                            .cancelled(false)
                            .build();

                    sessionRepository.save(session);
                    log.info("Session created successfully on {}", startDate);
                }

            }

        }
        log.info("[SessionService].[createSessions] executed successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    @Transactional
    public Response cancelSessions(List<Integer> sessionIds) {
        log.info("executing method [SessionService].[cancelSessions]");

        List<Session> sessions = sessionRepository.findByIdIn(sessionIds);

        for (Session session : sessions){
            boolean isAssignedToSession = sessionRepository.isAuthorizedToTakeAttendanceForSession(staffService.getStaffId(), session.getId());
            boolean isAssignedToClass = classRepository.isAuthorizedToTakeAttendanceForClass(staffService.getStaffId(), session.getClass_().getId());
            boolean isInstructorOnly = staffRepository.isInstructorOnly(staffService.getStaffId());
            // idk why i put the above query pls revise and revise this logic tbh I think to make sure admins/staff don't get validated?
            if ((isAssignedToClass || isAssignedToSession) && isInstructorOnly){
                log.error("Staff instructor is not assigned to this class/session");
                throw new HandledRejection("المُدرّس غير مُعيّن في هذا الصف لإلغاء الحصة");
            }

            if (session.getCancelled()){
                log.error("Staff tried to cancel a cancelled session {}", session);
                throw new HandledRejection("لا يمكن إلغاء حصة تم إلغاؤها مسبقًا");
            }

            if(!session.getSemester().getActive()){
                log.error("Staff tried to cancel a session from a closed semester {}", session);
                throw new HandledRejection("لا يمكن إلغاء حصة من فصل منتهٍ");
            }

            session.setCancelled(true);
        }

        sessionRepository.saveAll(sessions);

        log.info("[SessionService].[cancelSessions] executed successfully");
        return new Response("تم إلغاء الحصص بنجاح");
    }

}
