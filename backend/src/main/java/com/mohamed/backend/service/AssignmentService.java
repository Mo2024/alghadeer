package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.classinfo.assignment.Assignment;
import com.mohamed.backend.repository.classinfo.ClassRepository;
import com.mohamed.backend.repository.classinfo.assignment.AssignmentRepository;
import com.mohamed.backend.repository.user.StaffRepository;
import com.mohamed.backend.utils.Logger;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    private final ClassRepository classRepository;

    private final StaffService staffService;

    private final StaffRepository staffRepository;

    private final Logger logger;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Assignment createAssignment(Assignment assignmentReq, Integer classId) throws JsonProcessingException {

        if (assignmentReq.getStartDateTime() == null) {
            log.error("Start date/time must not be null");
            throw new HandledRejection("تاريخ ووقت البدء مطلوب");
        }

        if (assignmentReq.getEndDateTime() == null) {
            log.error("End date/time must not be null");
            throw new HandledRejection("تاريخ ووقت الانتهاء مطلوب");
        }

        if (assignmentReq.getEndDateTime().isBefore(assignmentReq.getStartDateTime())) {
            log.error("End date/time cannot be before start date/time");
            throw new HandledRejection("تاريخ ووقت الانتهاء لا يمكن أن يكون قبل تاريخ ووقت البدء");
        }

        if (assignmentReq.getTotalGrade() == null) {
            log.error("Total grade must not be null");
            throw new HandledRejection("الدرجة الكلية مطلوبة");
        }

        if (assignmentReq.getTotalGrade() < 0) {
            log.error("Total grade must not be negative");
            throw new HandledRejection("الدرجة الكلية لا يمكن أن تكون سالبة");
        }

        log.info("Calling [classRepository].[findByIdAndSemesterActiveTrue]");
        Class class_ = classRepository.findByIdAndSemesterActiveTrue(classId)
                .orElseThrow(() -> {
                    log.error("Class ID does not exist or semester is not active:{}", classId);
                    throw new HandledRejection("الصف غير موجود أو الفصل الدراسي غير نشط");
                });
        log.info("[classRepository].[findByIdAndSemesterActiveTrue] called successfully");

        log.info("Calling [classRepository].[isAuthorizedToTakeAttendanceForClass]");
        boolean isAssignedToClass = classRepository.isAuthorizedToTakeAttendanceForClass(staffService.getStaffId(), classId);
        log.info("[classRepository].[isAuthorizedToTakeAttendanceForClass] called successfully");

        log.info("Calling [staffRepository].[isInstructorOnly]");
        boolean isInstructorOnly = staffRepository.isInstructorOnly(staffService.getStaffId());
        log.info("[staffRepository].[isInstructorOnly] called successfully");

        // idk why i put the above query pls revise and revise this logic tbh I think to make sure admins/staff don't get validated?
        if (!isAssignedToClass && isInstructorOnly) {
            log.error("Staff instructor is not assigned to this class");
            throw new HandledRejection("المُدرّس غير مُعيّن في هذا الصف");
        }


        Assignment assignment = Assignment.builder()
                .startDateTime(assignmentReq.getStartDateTime())
                .endDateTime(assignmentReq.getEndDateTime())
                .totalGrade(assignmentReq.getTotalGrade())
                .class_(class_)
                .build();

        log.info("Calling [assignmentRepository].[save]");
        assignmentRepository.save(assignment);
        log.info("[assignmentRepository].[save] called successfully");

        logger.logJsonObject("Assignment created successfully\n {}", assignment);

        return assignment;
    }

}
