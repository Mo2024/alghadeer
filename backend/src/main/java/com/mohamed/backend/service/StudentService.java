package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.dto.semester.SemesterEnrollmentView;

import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.repository.semester.SemesterEnrollmentRepository;
import com.mohamed.backend.repository.semester.SemesterRepository;
import com.mohamed.backend.security.StudentDetails;
import com.mohamed.backend.utils.HashUtils;
import com.mohamed.backend.utils.ImageUtils;
import com.mohamed.backend.utils.Logger;
import com.mohamed.backend.utils.ValidationUtils;
import com.mohamed.backend.dto.user.Login;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.user.Student;
import com.mohamed.backend.repository.user.StudentRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final ImageUtils imageUtils;
    private final SemesterRepository semesterRepository;
    private final SemesterEnrollmentRepository semesterEnrollmentRepository;
    private final Logger logger;

    public Page<Student> getStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Integer getStudentId() {
        StudentDetails studentDetails = (StudentDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentDetails.getId();
    }

    @Transactional
    public Response register(Student student, MultipartFile image, HttpSession session) throws IOException {
        logger.logJsonObject("Request parameter:\n{}", student);


        if (student.getName() == null || student.getName().trim().isEmpty() || !ValidationUtils.isArabic(student.getName())) {
            log.error("Invalid name:\n{}", student.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        if (student.getArea() == null || student.getArea().trim().isEmpty() || !ValidationUtils.isArabic(student.getArea())) {
            log.error("Invalid area: {}", student.getArea());
            throw new HandledRejection("يرجى التأكد من إدخال اسم المنطقة بشكل صحيح وباللغة العربية");
        }

        if (student.getCpr() == null || !ValidationUtils.isValidCpr(student.getCpr())) {
            log.error("Invalid CPR:\n{}", student.getCpr());
            throw new HandledRejection("يرجى التأكد من إدخال الرقم الشخصي بشكل صحيح");
        }

        if (student.getTelephone() == null || !ValidationUtils.isValidTelephone(student.getTelephone())) {
            log.error("Invalid telephone:\n{}", student.getTelephone());
            throw new HandledRejection("يرجى التأكد من إدخال رقم الهاتف بشكل صحيح");
        }

        if (student.getEmail() == null || student.getEmail().trim().isEmpty() || !ValidationUtils.isValidEmail(student.getEmail())) {
            log.error("Invalid email:\n{}", student.getEmail());
            throw new HandledRejection("يرجى التأكد من إدخال البريد الإلكتروني بشكل صحيح");
        }

        if (student.getDateOfBirth() == null || !ValidationUtils.isPastDate(student.getDateOfBirth())) {
            log.error("Invalid date of birth:\n{}", student.getDateOfBirth());
            throw new HandledRejection("يرجى التأكد من إدخال تاريخ ميلاد بشكل صحيح");
        }

        log.info("Calling [studentRepository].[existsByCpr]");
        if (studentRepository.existsByCpr(student.getCpr())) {
            log.error("Duplicate CPR registration attempt:\n{}", student.getCpr());
            throw new HandledRejection("رقم الهوية مسجل من قبل");
        }
        log.info("[studentRepository].[existsByCpr] called successfully");

        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                log.error("Invalid image content type:\n{}", contentType);
                throw new HandledRejection("يرجى تحميل صورة بصيغة JPEG أو PNG");
            }

            log.info("Uploaded image - filename: {}, size: {} bytes, type: {}",
                    image.getOriginalFilename(), image.getSize(), contentType);

            byte[] resizedImage = imageUtils.resizeAndCompress(image);
            imageUtils.saveImageToFile(resizedImage, student.getCpr());
            log.info("Image saved to file successfully for student:\n{}", student.getCpr());
        } else {
            log.info("No image uploaded or image is empty");
        }

        String cleanEmail = student.getEmail().trim().toLowerCase();

        Student newStudent = Student.builder()
                .cpr(student.getCpr())
                .area(student.getArea())
                .name(student.getName())
                .email(cleanEmail)
                .hash(HashUtils.sha256(student.getCpr()))
                .dateOfBirth(student.getDateOfBirth())
                .telephone(student.getTelephone())
                .build();

        log.info("Calling [studentRepository].[save]");
        newStudent = studentRepository.save(newStudent);
        log.info("[studentRepository].[save] called successfully");

        log.info("New student saved with ID:\n{}", newStudent.getId());

        StudentDetails studentDetails = new StudentDetails(newStudent);

        Map<String, Boolean> permissionBooleanMap = new HashMap<>();
        permissionBooleanMap.put("STUDENT", true);
        newStudent.setPermissionBooleanMap(permissionBooleanMap);

        Authentication authentication = new UsernamePasswordAuthenticationToken(studentDetails, null, studentDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        log.info("Session created for student ID:\n{}", newStudent.getId());

        logger.logJsonObject("Registration completed successfully:\n{}", newStudent);

        return new Response("تم التسجيل بنجاح", studentDetails.getPermissions());
    }

    public Response login(Login login, HttpSession session) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", login);

        log.info("Calling [studentRepository].[findByCpr]");
        Student student = studentRepository.findByCpr(login.getUsername())
                .orElseThrow(() -> {
                    log.error("Student not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[studentRepository].[findByCpr] called successfully");

        StudentDetails studentDetails = new StudentDetails(student);

        logger.logJsonObject("Student info:\n{}", student);

        if (login.getUsername() == null || login.getPassword() == null ||
            login.getUsername().isBlank() || login.getPassword().isBlank()) {
            log.error("Invalid login input");
            throw new HandledRejection("الرجاء إدخال اسم المستخدم وكلمة المرور");
        } else if (!student.getHash().equals(HashUtils.sha256(login.getPassword()))) {
            log.error("Invalid login attempt");
            throw new HandledRejection("اسم المستخدم أو كلمة المرور غير صحيحة");
        } else if (student.getHash().equals(HashUtils.sha256(login.getPassword()))) {
            Map<String, Boolean> permissionBooleanMap = new HashMap<>();
            permissionBooleanMap.put("STUDENT", true);
            student.setPermissionBooleanMap(permissionBooleanMap);

            Authentication authentication = new UsernamePasswordAuthenticationToken(studentDetails, null, studentDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            log.info("Student ID:\n{} logged in", student.getId());
        }
        return new Response("تم تسجيل الدخول بنجاح", studentDetails.getPermissions());
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR')")
    public List<SemesterEnrollmentView> getEnrolledStudents() throws JsonProcessingException {

        log.info("Calling [semesterRepository].[findByActive]");
        Semester semester = semesterRepository.findByActive(true)
                .orElseThrow(() -> {
                    log.error("No active semester found");
                    return new HandledRejection("لا يوجد فصل دراسي نشط حالياً");
                });
        log.info("[semesterRepository].[findByActive] called successfully");

        logger.logJsonObject("Semester Details:\n{}", semester);

        return semesterEnrollmentRepository.findAllBySemesterId(semester.getId());
    }

}