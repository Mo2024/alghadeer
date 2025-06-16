package com.mohamed.backend.service;

import com.mohamed.backend.Utils.HashUtils;
import com.mohamed.backend.Utils.ImageUtils;
import com.mohamed.backend.Utils.ValidationUtils;
import com.mohamed.backend.model.Student;
import com.mohamed.backend.repository.StudentRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ImageUtils imageUtils;

    public Page<Student> getStudents(Pageable pageable){
        return studentRepository.findAll(pageable);
    }

    @Transactional
    public Integer registerStudent(Student student, MultipartFile image, HttpSession session) throws IOException {
        if (student.getName() == null || student.getName().trim().isEmpty() || !ValidationUtils.isArabic(student.getName())) {
            throw new RuntimeException("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        if (student.getArea() == null || student.getArea().trim().isEmpty() || !ValidationUtils.isArabic(student.getArea())) {
            throw new RuntimeException("يرجى التأكد من إدخال اسم المنطقة بشكل صحيح وباللغة العربية");
        }

        if (student.getCpr() == null || !ValidationUtils.isValidCpr(student.getCpr())) {
            throw new RuntimeException("يرجى التأكد من إدخال الرقم الشخصي بشكل صحيح");
        }

        if (student.getTelephone() == null || !ValidationUtils.isValidTelephone(student.getTelephone())) {
            throw new RuntimeException("يرجى التأكد من إدخال رقم الهاتف بشكل صحيح");
        }

        if (student.getEmail() == null || student.getEmail().trim().isEmpty() || !ValidationUtils.isValidEmail(student.getEmail())) {
            throw new RuntimeException("يرجى التأكد من إدخال البريد الإلكتروني بشكل صحيح");
        }

        if (student.getDateOfBirth() == null || !ValidationUtils.isPastDate(student.getDateOfBirth())) {
            throw new RuntimeException("يرجى التأكد من إدخال تاريخ ميلاد بشكل صحيح");
        }

        String contentType = image.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new RuntimeException("يرجى تحميل صورة بصيغة JPEG أو PNG");
        }

        if (studentRepository.findByCpr(student.getCpr()).isPresent()) {
            throw new RuntimeException("رقم الهوية مسجل من قبل");
        }


        byte[] resizedImage = imageUtils.resizeAndCompress(image);

        imageUtils.saveImageToFile(resizedImage, student.getCpr().toString());

        Student newStudent = Student.builder()
                .cpr(student.getCpr())
                .area(student.getArea())
                .name(student.getName())
                .email(student.getEmail())
                .hash(HashUtils.sha256(student.getCpr().toString()))
                .dateOfBirth(student.getDateOfBirth())
                .telephone(student.getTelephone())
                .build();

        newStudent = studentRepository.save(newStudent);

        session.setAttribute("studentId", newStudent.getId());

        return newStudent.getId();
    }
}
