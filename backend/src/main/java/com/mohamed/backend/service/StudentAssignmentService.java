package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.classinfo.assignment.Assignment;
import com.mohamed.backend.model.classinfo.assignment.StudentsAssignment;
import com.mohamed.backend.repository.classinfo.ClassRepository;
import com.mohamed.backend.repository.classinfo.assignment.AssignmentRepository;
import com.mohamed.backend.repository.classinfo.assignment.StudentsAssignmentRepository;
import com.mohamed.backend.repository.semester.SemesterRepository;
import com.mohamed.backend.repository.user.StaffRepository;
import com.mohamed.backend.utils.Logger;
import com.mohamed.backend.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentAssignmentService {

    private final StudentsAssignmentRepository studentsAssignmentRepository;
    private final SemesterRepository semesterRepository;
    private final StaffRepository staffRepository;
    private final StaffService staffService;
    private final ClassRepository classRepository;
    private final AssignmentRepository assignmentRepository;
    private final Logger logger;

    @Transactional
    public void createStudentAssignment(Integer assignmentId, Integer classId) {
        log.info("Paratmeters assignmentId: {}, classId {}", assignmentId, classId);

        log.info("Calling [studentsAssignmentRepository].[bulkCreateStudentAssignment]");
        int rowsInserted = studentsAssignmentRepository.bulkCreateStudentAssignment(assignmentId, classId);
        log.info("[studentsAssignmentRepository].[bulkCreateStudentAssignment] called successfully");

        log.info("Number of rows inserted: {}", rowsInserted);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    @Transactional
    public Response submitAssignment(StudentsAssignment studentsAssignmentReq) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n {}", studentsAssignmentReq);

        log.info("Calling [semesterRepository].[findByActive]");
        semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });
        log.info("[semesterRepository].[findByActive] called successfully");

        log.info("Calling [studentsAssignmentRepository].[findByIdAndStudentIdAndAssignmentId]");
        StudentsAssignment studentsAssignment = studentsAssignmentRepository.findByIdAndStudentIdAndAssignmentId(
                        studentsAssignmentReq.getId(),
                        studentsAssignmentReq.getStudent().getId(),
                        studentsAssignmentReq.getAssignment().getId()
                )
                .orElseThrow(() -> {
                    log.error("Assignment for student not found");
                    return new HandledRejection("لا يوجد نشاط للطالب المحدد");
                });
        log.info("[studentsAssignmentRepository].[findByIdAndStudentIdAndAssignmentId] called successfully");

        if (studentsAssignment.isAssignmentDone()) {
            log.error("Assignment already submitted for this student");
            throw new HandledRejection("تم تسليم النشاط لهذا الطالب من قبل");
        }

        if (!studentsAssignmentReq.getInstructorComments().isEmpty() && !ValidationUtils.isArabic(studentsAssignmentReq.getInstructorComments())) {
            log.error("Instructor comments must be written in Arabic only");
            throw new HandledRejection("تعليقات المعلم يجب أن تكون باللغة العربية فقط");
        }

        log.info("Calling [classRepository].[isAuthorizedToTakeAttendanceForClass]");
        boolean isAssignedToClass = classRepository.isAuthorizedToTakeAttendanceForClass(staffService.getStaffId(), studentsAssignment.getAssignment().getClass_().getId());
        log.info("[classRepository].[isAuthorizedToTakeAttendanceForClass] called successfully");

        log.info("Calling [staffRepository].[isInstructorOnly]");
        boolean isInstructorOnly = staffRepository.isInstructorOnly(staffService.getStaffId());
        log.info("[staffRepository].[isInstructorOnly] called successfully");

        // idk why i put the above query pls revise and revise this logic tbh I think to make sure admins/staff don't get validated?
        if (!isAssignedToClass && isInstructorOnly) {
            log.error("Staff instructor is not assigned to this class");
            throw new HandledRejection("المُدرّس غير مُعيّن في هذا الصف");
        }

        Integer grade = studentsAssignmentReq.getGrade();
        Integer totalGrade = studentsAssignment.getTotalGrade();

        if (grade == null || grade < 0 || (grade > totalGrade)) {
            log.error("Invalid grade {}", grade);
            throw new HandledRejection("الدرجة المدخلة غير صحصة");
        }

        studentsAssignment.setSubmissionDate(LocalDateTime.now());
        studentsAssignment.setAssignmentDone(true);
        studentsAssignment.setGrade(grade);
        studentsAssignment.setInstructorComments(studentsAssignmentReq.getInstructorComments());

        log.info("Calling [studentsAssignmentRepository].[save]");
        studentsAssignmentRepository.save(studentsAssignment);
        log.info("[studentsAssignmentRepository].[save] called successfully");

        return new Response("تم تقديم النشاط بنجاح");
    }
}
