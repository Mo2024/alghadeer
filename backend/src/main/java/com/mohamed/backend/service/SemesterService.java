package com.mohamed.backend.service;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.dto.SemesterDto;
import com.mohamed.backend.dto.SemesterView;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.enums.Grade;
import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.model.semester.SemesterEnrollment;
import com.mohamed.backend.model.user.Student;
import com.mohamed.backend.repository.classinfo.GradeClassAssignmentRepository;
import com.mohamed.backend.repository.semester.SemesterEnrollmentRepository;
import com.mohamed.backend.repository.semester.SemesterRepository;
import com.mohamed.backend.repository.user.StudentRepository;
import com.mohamed.backend.security.StudentDetails;
import com.mohamed.backend.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class SemesterService {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SemesterEnrollmentRepository semesterEnrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GradeClassAssignmentRepository gradeClassAssignmentRepository;

    @Autowired
    private ClassService classService;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    public Page<SemesterView> getSemesters(Pageable pageable){
        return semesterRepository.findAllByOrderByIdDesc(pageable);
    }


    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    @Transactional
    public Response createSemester(SemesterDto semesterReq) {
        log.info("executing method [createSemester]");

        log.info("Semester Request info:\n{}", semesterReq);

        if (semesterReq.getName() == null || semesterReq.getName().trim().isEmpty() || !ValidationUtils.isArabic(semesterReq.getName())) {
            log.error("Invalid name:\n{}", semesterReq.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        if (semesterReq.getSemester() == null || semesterReq.getSemester().toString().trim().isEmpty() || !ValidationUtils.isValidSemester(semesterReq.getSemester().toString())) {
            log.error("Invalid Invalid Semester:\n{}", semesterReq.getSemester());
            throw new HandledRejection("يرجى التأكد من إدخال الفصل صحيح");
        }

        if (semesterReq.getStartDate() == null || ValidationUtils.isPastDate(semesterReq.getStartDate())) {
            log.error("Invalid start date: {}", semesterReq.getStartDate());
            throw new HandledRejection("يرجى التأكد من إدخال تاريخ إبتداء الفصل بشكل صحيح");
        }

        if (semesterReq.getEndDate() == null || ValidationUtils.isPastDate(semesterReq.getEndDate())) {
            log.error("Invalid end date: {}", semesterReq.getEndDate());
            throw new HandledRejection("يرجى التأكد من إدخال تاريخ إنتهاء الفصل بشكل صحيح");
        }

        if (semesterReq.getStartDate().isAfter(semesterReq.getEndDate())) {
            log.error("Start date is after end date: {} > {}", semesterReq.getStartDate(), semesterReq.getEndDate());
            throw new HandledRejection("تاريخ البداية يجب أن يكون قبل تاريخ النهاية");
        }


        if (semesterRepository.existsByYearAndSemester(semesterReq.getStartDate().getYear(), semesterReq.getSemester())) {
            log.error("Duplicate semester entry:\n{} - {}", semesterReq.getStartDate().getYear(), semesterReq.getSemester());
            throw new HandledRejection("الفصل الدراسي مسجل مسبقًا");
        }

        if (semesterRepository.existsOverlappingSemester(semesterReq.getStartDate(), semesterReq.getEndDate())) {
            log.error("Overlapping semester detected:\nStartDate: {}, EndDate: {}", semesterReq.getStartDate(), semesterReq.getEndDate());
            throw new HandledRejection("يوجد فصل دراسي يتداخل مع هذا التاريخ");
        }


        if (semesterRepository.existsByActive(true)) {
            log.error("Cannot create semester while another is active");
            throw new HandledRejection("يجب إغلاق جميع الفصول الدراسية قبل إنشاء فصل جديد");
        }

        Semester semester = Semester.builder()
                .name(semesterReq.getName())
                .semester(semesterReq.getSemester())
                .startDate(semesterReq.getStartDate())
                .endDate(semesterReq.getEndDate())
                .active(true)
                .defaultClasses(semesterReq.isDefaultClasses())
                .build();

        semesterRepository.save(semester);

        int count = 0;
        for (Class class_ : semesterReq.getClasses()) {
            count += class_.getGradeClassAssignments() == null ? 0 : class_.getGradeClassAssignments().size();
        }

        if (count != 12) {
            log.error("Grade Class Assignments is not equal to 12 (total grades)");
            throw new HandledRejection("عدد تعيينات الصف الدراسي لا يساوي 12 (إجمالي الصفوف)");
        }

        if(semesterReq.isDefaultClasses()){
            log.info("calling class service...");
            classService.createDefaultClasses(semester, semesterReq.getClasses());
        }  else {
            log.info("calling class service...");
            classService.createCustomClasses(semesterReq.getClasses(), semester);
        }

        log.info("Semester saved to DB successfully:\n{}", semester);

        log.info("[createSemester] executed successfully");
        return new Response("تم إنشاء الفصل بنجاح");
    }


    @PreAuthorize("isAuthenticated() and hasAnyRole('STUDENT')")
    @Transactional
    public Response enrollSemester(Grade grade) {
        log.info("executing method [SemesterService].[enrollSemester]");

        StudentDetails studentDetails = (StudentDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });

        log.info("Current active semester\n{}", semester);

        Student student = studentRepository.findById(studentDetails.getId())
                .orElseThrow(() -> {
                    log.error("Student does not exist:\n{}", studentDetails.getId());
                    return new HandledRejection("الطالب غير موجود");
                });

        // I do not need to validate here if the semester is active because above the semester is already fetched by activeness
        Class class_ = gradeClassAssignmentRepository.findBySemesterIdAndGrade(semester.getId(), grade).getClass_();
        log.info("Fetched Class:\n {}", class_);
        student.getClasses().add(class_);
        studentRepository.save(student);


        log.info("Student enrolling\n{}", student);

        if(semesterEnrollmentRepository.existsByStudentIdAndSemesterId(studentDetails.getId(), semester.getId())){
            log.error("Student already registered in semester");
            throw new HandledRejection("لا يمكن تسجيل الطالب لأنه مسجل في هذا الفصل الدراسي");
        }

        SemesterEnrollment semesterEnrollment = SemesterEnrollment.builder()
                .student(student)
                .semester(semester)
                .enrollmentDate(LocalDate.now())
                .build();

        semesterEnrollmentRepository.save(semesterEnrollment);

        log.info("Semester Enrollment saved successfully:\n{} ", semesterEnrollment);

        log.info("[SemesterService].[enrollSemester] executed successfully");
        return new Response("تم تسجيل الطالب في الفصل الدراسي بنجاح");
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    @Transactional
    public Page<SemesterView> closeActiveSemester(Pageable pageable) {
        log.info("executing method [SemesterService].[closeActiveSemester]");

        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });

        log.info("Semester Details:\n{}", semester);

        semester.setActive(false);
        semesterRepository.save(semester);

        log.info("Semester ID={} closed successfully", semester.getId());

        log.info("[SemesterService].[closeActiveSemester] executed successfully");
        return semesterRepository.findAllByOrderByIdDesc(pageable);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('STUDENT')")
    @Transactional
    public boolean isEnrolled() {

        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });

        return semesterEnrollmentRepository.existsByStudentIdAndSemesterId(studentService.getStudentId(), semester.getId());
    }
}
