package com.mohamed.backend.service;

import com.mohamed.backend.Utils.ValidationUtils;
import com.mohamed.backend.model.Student;
import com.mohamed.backend.repository.StudentRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Page<Student> getStudents(Pageable pageable){
        return studentRepository.findAll(pageable);
    }

    @Transactional
    public Integer registerStudent(Student student, HttpSession session){
        if (student.getName() == null || student.getName().trim().isEmpty() || !ValidationUtils.isArabic(student.getName())) {
            throw new RuntimeException("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        if (student.getArea() == null || student.getArea().trim().isEmpty() || !ValidationUtils.isArabic(student.getArea())) {
            throw new RuntimeException("يرجى التأكد من إدخال اسم المنطقة بشكل صحيح وباللغة العربية");
        }

        if (student.getCpr() == null || !ValidationUtils.isValidCpr(student.getCpr())) {
            throw new RuntimeException("يرجى التأكد من إدخال رقم الهوية المنطقة بشكل صحيح");
        }

    }
}
