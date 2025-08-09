package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.dto.*;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.classinfo.Attendance;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.classinfo.ClassSchedule;
import com.mohamed.backend.model.classinfo.Session;
import com.mohamed.backend.repository.classinfo.AttendanceRepository;
import com.mohamed.backend.repository.classinfo.ClassRepository;
import com.mohamed.backend.repository.classinfo.SessionRepository;
import com.mohamed.backend.repository.user.StaffRepository;
import com.mohamed.backend.repository.user.StudentRepository;
import com.mohamed.backend.utils.Logger;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private Logger logger;

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public GetAttendanceStatusDto takeAttendance(AttendanceRequestDTO attendanceRequest) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", attendanceRequest);

        Integer sessionId = attendanceRequest.getSession().getId();
        log.info("Calling [sessionRepository].[findById]");
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.error("Session not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[sessionRepository].[findById] called successfully");

        log.info("Calling [classRepository].[findById]");
        Class class_ = classRepository.findById(session.getSemesterClass().getId())
                .orElseThrow(() -> {
                    log.error("class not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[classRepository].[findById] called successfully");

        if (LocalDate.now().isBefore(session.getDate())) {
            log.error("Staff tried to take attendance before date of session {}", session.getDate());
            throw new HandledRejection("لا يمكنك تسجيل الحضور قبل تاريخ الحصة");
        } else if (LocalDate.now().isEqual(session.getDate()) || LocalDate.now().isAfter(session.getDate())) {
            LocalTime startTime = class_.getClassSchedules().stream()
//                    .filter(schedule -> schedule.getDayOfWeek().name().equals(LocalDate.now().getDayOfWeek().name())) check this
                    .map(ClassSchedule::getStartTime)
                    .findFirst()
                    .orElseThrow(() -> {
                        try {
                            logger.logJsonObjectError("No schedule for this class:\n{}", class_);
                        } catch (JsonProcessingException e) {
                            log.error("Failed to log attendanceRequest", e);
                        }
                        return new HandledRejection("لا يوجد جدول زمني لهذا الفصل");
                    });
            if (LocalTime.now().isBefore(startTime) && LocalDate.now().isBefore(session.getDate())) { // check this
                logger.logJsonObjectError("Staff tried to take attendance before class start time:\n{}", class_);
                throw new HandledRejection("لا يمكنك تسجيل الحضور قبل وقت بدء الحصة");
            }
        }

        if (session.getCancelled()) {
            logger.logJsonObjectError("Staff tried to take attendance to a cancelled session:\n{}", session);
            throw new HandledRejection("لا يمكنك تسجيل الحضور لحصة ملغاة");
        }
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
            throw new HandledRejection("المُدرّس غير مُعيّن في هذا الصف لأخذ الحضور");
        }

        Set<Integer> seenStudents = new HashSet<>();
        for (Attendance attendance : attendanceRequest.getAttendances()) {
            log.info("Calling [classRepository].[isStudentInClass]");
            if (!classRepository.isStudentInClass(attendance.getStudent().getId(), session.getSemesterClass().getId())) {
                log.error("Student below is not assigned to the class:\n {}", attendance.getStudent());
                throw new HandledRejection("بعض الطلاب غير مسجلين في هذا الفصل");
            }
            log.info("[classRepository].[isStudentInClass] called successfully");

            int studentId = attendance.getStudent().getId();
            log.info("Calling [classRepository].[isDuplicateAttendance]");
            if (!seenStudents.add(studentId) || classRepository.isDuplicateAttendance(studentId, sessionId)) {
                log.error("Duplicate attendance found for student id {}", studentId);
                throw new HandledRejection("توجد تكرارات في تسجيل الحضور لنفس الطالب");
            }
            log.info("[classRepository].[isDuplicateAttendance] called successfully");

            attendance.setSession(session);
        }

        log.info("Calling [attendanceRepository].[saveAll]");
        attendanceRepository.saveAll(attendanceRequest.getAttendances());
        log.info("[attendanceRepository].[saveAll] called successfully");

        return getAttendanceStatus(attendanceRequest.getSession().getId(), attendanceRequest.getSession().getSemesterClass().getId());
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public GetAttendanceStatusDto getAttendanceStatus(int sessionId, int classId) throws JsonProcessingException {
        log.info("Request parameter sessionId {} classId {}", sessionId, classId);

        GetAttendanceStatusDto response = new GetAttendanceStatusDto();

        log.info("Calling [attendanceRepository].[isAttendanceTaken]");
        boolean isAttendanceTaken = attendanceRepository.countAttendanceBySessionId(sessionId) > 0;
        log.info("[attendanceRepository].[isAttendanceTaken] called successfully: {}", isAttendanceTaken);

        response.setAttendanceTaken(isAttendanceTaken);

        if (isAttendanceTaken) {
            log.info("Calling [attendanceRepository].[findBySessionId]");
            List<AttendanceView> attendanceList = attendanceRepository.findBySessionId(sessionId);
            logger.logJsonObject("[attendanceRepository].[findBySessionId] called successfully:\n{}", attendanceList);
            response.setAttendanceList(attendanceList);
            response.setStudents(null);
        } else {
            log.info("Calling [classRepository].[findStudentByClassId]");
            List<StudentAttendanceView> students = classRepository.findStudentByClassId(classId);
            log.info("[classRepository].[findStudentByClassId] called successfully:\n{}", students);
            response.setStudents(students);
            response.setAttendanceList(null);
        }

        return response;
    }
}
