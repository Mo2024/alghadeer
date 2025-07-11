package com.mohamed.backend.service;

import com.mohamed.backend.dto.AttendanceRequestDTO;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.classinfo.Attendance;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.classinfo.ClassSchedule;
import com.mohamed.backend.model.classinfo.Session;
import com.mohamed.backend.model.user.Staff;
import com.mohamed.backend.repository.AttendanceRepository;
import com.mohamed.backend.repository.ClassRepository;
import com.mohamed.backend.repository.SessionRepository;
import com.mohamed.backend.repository.StaffRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
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

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response takeAttendance(AttendanceRequestDTO attendanceRequest){
        log.info("executing method [takeAttendance]");

        Integer sessionId = attendanceRequest.getSession().getId();
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.error("Session not found");
                    return new UnhandledRejection("يرجى التأكد من البيانات");
                });
        Class class_ = classRepository.findById(session.getClass_().getId())
                .orElseThrow(() -> {
                    log.error("class not found");
                    return new UnhandledRejection("يرجى التأكد من البيانات");
                });

        if(LocalDate.now().isBefore(session.getDate())){
            log.error("Staff tried to take attendance before date of session {}", session.getDate());
            throw new UnhandledRejection("لا يمكنك تسجيل الحضور قبل تاريخ الحصة");
        } else if (LocalDate.now().isEqual(session.getDate()) || LocalDate.now().isAfter(session.getDate())) {
            LocalTime startTime = class_.getClassSchedules().stream()
                    .filter(schedule -> schedule.getDayOfWeek().name().equals(LocalDate.now().getDayOfWeek().name()))
                    .map(ClassSchedule::getStartTime)
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("No schedule for this class:\n{}", class_);
                        return new UnhandledRejection("لا يوجد جدول زمني لهذا الفصل");
                    });
            if(LocalTime.now().isBefore(startTime)){
                log.error("Staff tried to take attendance before class start time {}", class_);
                throw new UnhandledRejection("لا يمكنك تسجيل الحضور قبل وقت بدء الحصة");
            }
        }

        if(session.getCancelled()){
            log.error("Staff tried to take attendance to a cancelled session {}", session);
            throw new UnhandledRejection("لا يمكنك تسجيل الحضور لحصة ملغاة");
        }

        boolean isAssigned = staffRepository.isAuthorizedToTakeAttendance(staffService.getStaffId(), session.getClass_().getId());
        boolean isInstructorOnly = staffRepository.isInstructorOnly(staffService.getStaffId());

        if (!isAssigned && isInstructorOnly){
            log.error("Staff instructor is not assigned to this class");
            throw new UnhandledRejection("المُدرّس غير مُعيّن في هذا الفصل الدراسي لأخذ الحضور");
        }

        Set<Integer> seenStudents = new HashSet<>();
        for (Attendance attendance : attendanceRequest.getAttendances()){
            if(!classRepository.isStudentInClass(attendance.getStudent().getId(), session.getClass_().getId())){
                log.error("Student below is not assigned to the class:\n {}", attendance.getStudent());
                throw new UnhandledRejection("بعض الطلاب غير مسجلين في هذا الفصل");
            }
            int studentId = attendance.getStudent().getId();
            if (!seenStudents.add(studentId) || classRepository.isDuplicateAttendance(studentId, sessionId)) {
                log.error("Duplicate attendance found for student id {}", studentId);
                throw new UnhandledRejection("توجد تكرارات في تسجيل الحضور لنفس الطالب");
            }
            attendance.setSession(session);
        }

        attendanceRepository.saveAll(attendanceRequest.getAttendances());

        log.info("[takeAttendance] executed successfully");
        return new Response("تم تسجيل الحضور بنجاح");
    }
}
