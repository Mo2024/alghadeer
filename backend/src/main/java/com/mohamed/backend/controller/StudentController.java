package com.mohamed.backend.controller;

import com.mohamed.backend.dto.ErrorResponse;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.Student;
import com.mohamed.backend.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@ModelAttribute Student student, @RequestParam("image") MultipartFile image, HttpSession session){
        try {
            Integer studentId = studentService.registerStudent(student, image, session);
            return ResponseEntity.ok().body(studentId);
        } catch (UnhandledRejection e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()) {
            });
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
