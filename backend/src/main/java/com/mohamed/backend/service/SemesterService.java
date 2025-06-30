package com.mohamed.backend.service;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.Semester;
import com.mohamed.backend.model.SemesterEnrollment;
import com.mohamed.backend.model.Staff;
import com.mohamed.backend.model.Student;
import com.mohamed.backend.repository.SemesterEnrollmentRepository;
import com.mohamed.backend.repository.SemesterRepository;
import com.mohamed.backend.repository.StudentRepository;
import com.mohamed.backend.security.StudentDetails;
import com.mohamed.backend.utils.HashUtils;
import com.mohamed.backend.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private SemesterEnrollmentRepository semesterEnrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Response createSemester(Semester semesterReq) {
        log.info("Semester Request info:\n{}", semesterReq);

        if (semesterReq.getName() == null || semesterReq.getName().trim().isEmpty() || !ValidationUtils.isArabic(semesterReq.getName())) {
            log.error("Invalid name:\n{}", semesterReq.getName());
            throw new UnhandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        if (semesterReq.getSemester() == null || semesterReq.getSemester().toString().trim().isEmpty() || !ValidationUtils.isValidSemester(semesterReq.getSemester().toString())) {
            log.error("Invalid Invalid Semester:\n{}", semesterReq.getSemester());
            throw new UnhandledRejection("يرجى التأكد من إدخال الفصل صحيح");
        }

        if (semesterReq.getYear() == null || !ValidationUtils.isValidYear(semesterReq.getYear())) {
            log.error("Invalid year:\n{}", semesterReq.getYear());
            throw new UnhandledRejection("يرجى التأكد من إدخال السنة بشكل صحيح");
        }


        if (semesterRepository.existsByYearAndSemester(semesterReq.getYear(), semesterReq.getSemester())) {
            log.error("Duplicate semester entry:\n{} - {}", semesterReq.getYear(), semesterReq.getSemester());
            throw new UnhandledRejection("الفصل الدراسي مسجل مسبقًا");
        }

        if (semesterRepository.existsByActive(true)) {
            log.error("Cannot create semester while another is active");
            throw new UnhandledRejection("يجب إغلاق جميع الفصول الدراسية قبل إنشاء فصل جديد");
        }

        Semester semester = Semester.builder()
                .name(semesterReq.getName())
                .semester(semesterReq.getSemester())
                .year(semesterReq.getYear())
                .active(true)
                .build();

        semesterRepository.save(semester);

        log.info("Semester saved to DB successfully:\n{}", semester);

        return new Response("تم إنشاء الفصل بنجاح");
    }


    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public Response enrollSemester() {
        StudentDetails studentDetails = (StudentDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new UnhandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });

        log.info("Current active semester\n{}", semester);

        Student student = studentRepository.findById(studentDetails.getId())
                .orElseThrow(() -> {
                    log.error("Student does not exist:\n{}", studentDetails.getId());
                    return new UnhandledRejection("الطالب غير موجود");
                });

        log.info("Student enrolling\n{}", student);

        if(semesterEnrollmentRepository.existsByStudentIdAndSemesterId(studentDetails.getId(), semester.getId())){
            log.error("Student already registered in semester");
            throw new UnhandledRejection("لا يمكن تسجيل الطالب لأنه مسجل في هذا الفصل الدراسي");
        }

        SemesterEnrollment semesterEnrollment = SemesterEnrollment.builder()
                .student(student)
                .semester(semester)
                .enrollmentDate(LocalDate.now())
                .build();

        semesterEnrollmentRepository.save(semesterEnrollment);

        log.info("Semester Enrollment saved successfully:\n{} ", semesterEnrollment);

        return new Response("تم تسجيل الطالب في الفصل الدراسي بنجاح");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Response switchSemesterStatus(Integer semesterId) {

        log.info("Semester ID:\n{}",semesterId);


        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> {
                    log.error("Semester does not exist");
                    return new UnhandledRejection("الفصل الدراسي غير موجود");
                });

        log.info("Semester Details:\n{}", semester);

        if(!semester.getActive() && semesterRepository.existsByActive(true)) {
            log.error("Cannot change semester status while another semester is active");
            throw new UnhandledRejection("لا يمكن تغيير الحالة إلى نشط طالما يوجد فصل دراسي نشط حالياً");
        }

        semester.setActive(!semester.getActive());
        semesterRepository.save(semester);
        log.info("Semester status changed from {} to {} successfully", semester.getActive(), !semester.getActive());

        return new Response("تم تحديث حالة الفصل الدراسي بنجاح");
    }
}
