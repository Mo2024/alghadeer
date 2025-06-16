package com.mohamed.backend.service;

import com.mohamed.backend.Utils.HashUtils;
import com.mohamed.backend.Utils.ImageUtils;
import com.mohamed.backend.Utils.ValidationUtils;
import com.mohamed.backend.dto.Login;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.Student;
import com.mohamed.backend.repository.StudentRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ImageUtils imageUtils;

    public Page<Student> getStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Transactional
    public Response register(Student student, MultipartFile image, HttpSession session) throws IOException {
        log.info("Registering student: {}", student);
        log.info("Uploaded image - filename: {}, size: {} bytes, type: {}",
                image.getOriginalFilename(), image.getSize(), image.getContentType());

        if (student.getName() == null || student.getName().trim().isEmpty() || !ValidationUtils.isArabic(student.getName())) {
            log.error("Invalid name: {}", student.getName());
            throw new UnhandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        if (student.getArea() == null || student.getArea().trim().isEmpty() || !ValidationUtils.isArabic(student.getArea())) {
            log.error("Invalid area: {}", student.getArea());
            throw new UnhandledRejection("يرجى التأكد من إدخال اسم المنطقة بشكل صحيح وباللغة العربية");
        }

        if (student.getCpr() == null || !ValidationUtils.isValidCpr(student.getCpr())) {
            log.error("Invalid CPR: {}", student.getCpr());
            throw new UnhandledRejection("يرجى التأكد من إدخال الرقم الشخصي بشكل صحيح");
        }

        if (student.getTelephone() == null || !ValidationUtils.isValidTelephone(student.getTelephone())) {
            log.error("Invalid telephone: {}", student.getTelephone());
            throw new UnhandledRejection("يرجى التأكد من إدخال رقم الهاتف بشكل صحيح");
        }

        if (student.getEmail() == null || student.getEmail().trim().isEmpty() || !ValidationUtils.isValidEmail(student.getEmail())) {
            log.error("Invalid email: {}", student.getEmail());
            throw new UnhandledRejection("يرجى التأكد من إدخال البريد الإلكتروني بشكل صحيح");
        }

        if (student.getDateOfBirth() == null || !ValidationUtils.isPastDate(student.getDateOfBirth())) {
            log.error("Invalid date of birth: {}", student.getDateOfBirth());
            throw new UnhandledRejection("يرجى التأكد من إدخال تاريخ ميلاد بشكل صحيح");
        }

        String contentType = image.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            log.error("Invalid image content type: {}", contentType);
            throw new UnhandledRejection("يرجى تحميل صورة بصيغة JPEG أو PNG");
        }

        if (studentRepository.findByCpr(student.getCpr()).isPresent()) {
            log.error("Duplicate CPR registration attempt: {}", student.getCpr());
            throw new UnhandledRejection("رقم الهوية مسجل من قبل");
        }

        byte[] resizedImage = imageUtils.resizeAndCompress(image);
        imageUtils.saveImageToFile(resizedImage, student.getCpr());
        log.info("Image saved to file successfully for student: {}", student.getCpr());

        Student newStudent = Student.builder()
                .cpr(student.getCpr())
                .area(student.getArea())
                .name(student.getName())
                .email(student.getEmail())
                .hash(HashUtils.sha256(student.getCpr()))
                .dateOfBirth(student.getDateOfBirth())
                .telephone(student.getTelephone())
                .build();

        newStudent = studentRepository.save(newStudent);

        log.info("New student saved with ID: {}", newStudent.getId());

        session.setAttribute("studentId", newStudent.getId());

        log.info("Session created for student ID: {}", newStudent.getId());

        log.info("Registration completed successfully for CPR: {}", newStudent.getCpr());

        return new Response("تم التسجيل بنجاح");
    }

    public Response login(Login login, HttpSession session) {
        Student student = studentRepository.findByCpr(login.getUsername())
                .orElseThrow(() -> new UnhandledRejection("يرجى التأكد من البيانات"));

        log.info("Login info {}", login);
        log.info("Student info {}", student);


        if (login.getUsername() == null || login.getPassword() == null ||
                login.getUsername().isBlank() || login.getPassword().isBlank() ||
                !student.getHash().equals(HashUtils.sha256(login.getPassword()))) {
            log.error("Invalid login attempt");
            throw new UnhandledRejection("الرجاء إدخال اسم المستخدم وكلمة المرور");
        } else {
            session.setAttribute("studentId", student.getId());
            log.info("Student ID: {} logged in", student.getId());
        }
        return new Response("تم تسجيل الدخول بنجاح");
    }
}