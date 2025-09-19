package com.mohamed.backend.classes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.classes.classesSchedules.ClassScheduleRepository;
import com.mohamed.backend.classes.dto.ChangeStudentClassDto;
import com.mohamed.backend.classes.dto.ClassView;
import com.mohamed.backend.classes.dto.ClassView2;
import com.mohamed.backend.classes.gradeClassAssignments.GradeClassAssignment;
import com.mohamed.backend.classes.gradeClassAssignments.GradeClassAssignmentRepository;
import com.mohamed.backend.utils.Response;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.classes.gradeClassAssignments.Grade;
import com.mohamed.backend.semesters.Semester;
import com.mohamed.backend.sessions.SessionService;
import com.mohamed.backend.users.staff.Staff;
import com.mohamed.backend.semesters.SemesterRepository;
import com.mohamed.backend.users.staff.StaffRepository;
import com.mohamed.backend.users.staff.StaffService;
import com.mohamed.backend.users.students.StudentRepository;
import com.mohamed.backend.utils.methods.Defaults;
import com.mohamed.backend.utils.methods.Logger;
import com.mohamed.backend.utils.methods.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;
    private final StaffRepository staffRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final SessionService sessionService;
    private final GradeClassAssignmentRepository gradeClassAssignmentRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final StaffService staffService;
    private final Logger logger;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR')")
    public List<ClassView> getClassesFromActiveSemester() throws JsonProcessingException {
        log.info("Calling [semesterRepository].[findByActive]");
        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });
        log.info("[semesterRepository].[findByActive] called successfully");

        logger.logJsonObject("Semester Details:\n{}", semester);

        return classRepository.findAllBySemesterId(semester.getId());
    }

    @Transactional
    public void createDefaultClasses(Semester semester, List<Class> classesReq) throws JsonProcessingException { //pls put validatioin for classses length

        List<Class> classes = Defaults.getDefaultClasses(semester);
        log.info("Calling [classRepository].[saveAll]");
        classes = classRepository.saveAll(classes);
        log.info("[classRepository].[saveAll] called successfully");

        for (int i = 0; i < classes.size(); i++) {
            Class classReq = classesReq.get(i);
            Class class_ = classes.get(i);

            class_.setClassSchedules(classReq.getClassSchedules());
            class_.setStaff(classReq.getStaff());

            Integer staffId = class_.getStaff().getId();


            log.info("Calling [staffRepository].[findByIdAndArchived]");
            Staff staff = staffRepository.findByIdAndArchived(staffId, false)
                    .orElseThrow(() -> {
                        log.error("Invalid staff provided:\n{}", class_.getStaff());
                        throw new HandledRejection(" الطاقم غير صالح أو غير موجود");
                    });
            log.info("[staffRepository].[findByIdAndArchived] called successfully");

            if (class_.getClassSchedules().isEmpty()) {
                throw new HandledRejection("يجب أن يكون هناك جدول واحد على الأقل");
            }

            if (class_.getClassSchedules().stream()
                    .anyMatch(classSchedule -> ValidationUtils.validateSchedule(
                            classSchedule.getDayOfWeek(), classSchedule.getStartTime(), classSchedule.getEndTime()))) {
                log.error("Invalid schedules:\n{}", class_.getClassSchedules());
                throw new HandledRejection("يوجد جدول زمني غير صالح في الفصل");
            }

            class_.getClassSchedules().forEach(schedule -> schedule.setClass_(class_));
            class_.setSemester(semester);

            class_.setStaff(staff);
            log.info("Calling [classRepository].[save]");
            Class savedClass = classRepository.save(class_);
            log.info("[classRepository].[save] called successfully");

            log.info("Calling [classScheduleRepository].[saveAll]");
            classScheduleRepository.saveAll(class_.getClassSchedules());
            log.info("[classScheduleRepository].[saveAll] called successfully");

            Map<Integer, List<Grade>> gradeAssignmentsMap = Map.of(
                    0, List.of(Grade.FIRST, Grade.SECOND),
                    1, List.of(Grade.THIRD),
                    2, List.of(Grade.FOURTH),
                    3, List.of(Grade.FIFTH),
                    4, List.of(Grade.SIXTH),
                    5, List.of(Grade.SEVENTH),
                    6, List.of(Grade.EIGHTH),
                    7, List.of(Grade.NINTH),
                    8, List.of(Grade.TENTH, Grade.ELEVENTH, Grade.TWELFTH)
            );

            List<Grade> grades = gradeAssignmentsMap.get(i);
            if (grades != null) {
                List<GradeClassAssignment> assignments = grades.stream()
                        .map(grade -> new GradeClassAssignment(null, grade, class_.getSemester(), class_))
                        .collect(Collectors.toList());
                class_.setGradeClassAssignments(assignments);
                log.info("Calling [gradeClassAssignmentRepository].[saveAll]");
                gradeClassAssignmentRepository.saveAll(class_.getGradeClassAssignments());
                log.info("[gradeClassAssignmentRepository].[saveAll] called successfully");

            }


            log.info("Creating sessions for class ID: {}", savedClass.getId());
            sessionService.createSessions(class_);
            log.info("Sessions created successfully for class ID: {}", savedClass.getId());

            logger.logJsonObject("Class Created Successfully:\n{}", savedClass);
        }
    }

    @Transactional
    public void createCustomClasses(List<Class> classes, Semester semester) throws JsonProcessingException {

        for (Class class_ : classes) {
            if (class_.getName() == null || class_.getName().trim().isEmpty() || !ValidationUtils.isArabic(class_.getName())) {
                log.error("Invalid name:\n{}", class_.getName());
                throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
            }

            if (class_.getStaff() == null) {
                throw new HandledRejection("يجب تحديد الطاقم");
            }

            Integer staffId = class_.getStaff().getId();


            log.info("Calling [staffRepository].[findByIdAndArchived]");
            Staff staff = staffRepository.findByIdAndArchived(staffId, false)
                    .orElseThrow(() -> {
                        log.error("Invalid staff provided:\n{}", class_.getStaff());
                        throw new HandledRejection(" الطاقم غير صالح أو غير موجود");
                    });
            log.info("[staffRepository].[findByIdAndArchived] called successfully");

            if (class_.getClassSchedules().isEmpty()) {
                throw new HandledRejection("يجب أن يكون هناك جدول واحد على الأقل");
            }

            if (class_.getClassSchedules().stream()
                    .anyMatch(classSchedule -> ValidationUtils.validateSchedule(
                            classSchedule.getDayOfWeek(), classSchedule.getStartTime(), classSchedule.getEndTime()))) {
                log.error("Invalid schedules:\n{}", class_.getClassSchedules());
                throw new HandledRejection("يوجد جدول زمني غير صالح في الفصل");
            }

            class_.getClassSchedules().forEach(schedule -> schedule.setClass_(class_));
            class_.setSemester(semester);

            class_.setStaff(staff);
            log.info("Calling [classRepository].[save]");
            Class savedClass = classRepository.save(class_);
            log.info("[classRepository].[save] called successfully");

            log.info("Calling [classScheduleRepository].[saveAll]");
            classScheduleRepository.saveAll(class_.getClassSchedules());
            log.info("[classScheduleRepository].[saveAll] called successfully");

            if (class_.getGradeClassAssignments().isEmpty()) {
                throw new HandledRejection("يجب أن يكون هناك صف مدرسي واحد على الأقل");
            }

            class_.getGradeClassAssignments().forEach(gradeClassAssignment -> {
                if (gradeClassAssignment.getGrade() == null) {
                    log.error("Invalid Grade class Assignment:\n{}", class_.getGradeClassAssignments());
                    throw new HandledRejection("تعيين الصف الدراسي إلى الصف غير صحيح");
                }
                gradeClassAssignment.setSemester(class_.getSemester());
                gradeClassAssignment.setClass_(class_);
            });
            log.info("Calling [gradeClassAssignmentRepository].[saveAll]");
            gradeClassAssignmentRepository.saveAll(class_.getGradeClassAssignments());
            log.info("[gradeClassAssignmentRepository].[saveAll] called successfully");

            log.info("Creating sessions for class ID: {}", savedClass.getId());
            sessionService.createSessions(class_);
            log.info("Sessions created successfully for class ID: {}", savedClass.getId());

            logger.logJsonObject("Class Created Successfully:\n{}", savedClass);
        }

    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR')")
    public Response changeStudentClass(ChangeStudentClassDto changeStudentClassDto) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", changeStudentClassDto);

        if (changeStudentClassDto.getStudentsId().isEmpty()) {
            log.warn("No students provided to transfer");
            throw new HandledRejection("لم يتم تقديم طلاب للتحويل");
        }

        log.info("Calling [classRepository].[findById]");
        Class class_ = classRepository.findById(changeStudentClassDto.getClassId())
                .orElseThrow(() -> {
                    log.error("class not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[classRepository].[findById] called successfully");

        log.info("Calling [classRepository].[countStudentsAlreadyInClass]");
        int alreadyInClassCount = classRepository.countStudentsAlreadyInClass(changeStudentClassDto.getClassId(), changeStudentClassDto.getStudentsId());
        log.info("[classRepository].[countStudentsAlreadyInClass] called successfully");

        if (alreadyInClassCount > 0) {
            log.error("Some students are already in this class");
            throw new HandledRejection("بعض الطلاب موجودين مسبقاً في الصف المحدد");
        }

        log.info("Calling [studentRepository].[countByIdIn]");
        int foundStudents = studentRepository.countByIdIn(changeStudentClassDto.getStudentsId());
        log.info("[studentRepository].[countByIdIn] called successfully");

        if (foundStudents != changeStudentClassDto.getStudentsId().size()) {
            log.error("Some students do not exist");
            throw new HandledRejection("بعض الطلاب غير مسجلين");
        }


        log.info("Calling [classRepository].[transferStudentsToClassInSemester]");
        classRepository.transferStudentsToClassInSemester(changeStudentClassDto.getStudentsId(), changeStudentClassDto.getClassId(), class_.getSemester().getId());
        log.info("[classRepository].[transferStudentsToClassInSemester] called successfully");

        log.info("Transferred {} students to class {} for semester {}",
                changeStudentClassDto.getStudentsId().size(),
                changeStudentClassDto.getClassId(),
                class_.getSemester().getId());

        return new Response("تم تغيير الصف بنجاح");
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<?> getAssignedClasses(boolean withSessionsANndAssignments) throws JsonProcessingException {

        log.info("Calling [semesterRepository].[findByActive]");
        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });
        log.info("[semesterRepository].[findByActive] called successfully");

        logger.logJsonObject("Semester Details:\n{}", semester);

        List<?> assignedClasses;

        if (withSessionsANndAssignments) {
            log.info("Calling [classRepository].[findAllClassView2ByStaffIdAndActiveSemester]");
            assignedClasses = classRepository.findAllClassView2ByStaffIdAndActiveSemester(staffService.getStaffId());
            log.info("[classRepository].[findAllClassView2ByStaffIdAndActiveSemester] called successfully");

        } else {
            log.info("Calling [classRepository].[findAllClassViewByStaffIdAndActiveSemester]");
            assignedClasses = classRepository.findAllClassViewByStaffIdAndActiveSemester(staffService.getStaffId());
            log.info("[classRepository].[findAllClassViewByStaffIdAndActiveSemester] called successfully");
        }


        logger.logJsonObject("Assigned classes:\n{}", assignedClasses);

        return assignedClasses;
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR')")
    public List<ClassView2> getActiveClassesWithSessions() throws JsonProcessingException {

        log.info("Calling [semesterRepository].[findByActive]");
        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });
        log.info("[semesterRepository].[findByActive] called successfully");

        logger.logJsonObject("Semester Details:\n{}", semester);

        log.info("Calling [classRepository].[findAllBySemesterActiveTrue]");
        List<ClassView2> assignedClasses = classRepository.findAllBySemesterActiveTrue();
        log.info("[classRepository].[findAllBySemesterActiveTrue] called successfully");

        logger.logJsonObject("Assigned classes:\n{}", assignedClasses);

        return assignedClasses;
    }

}
