package com.mohamed.backend.controller;

import com.mohamed.backend.dto.Login;
import com.mohamed.backend.dto.Response;
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
    public ResponseEntity<?> register(@ModelAttribute Student student, @RequestParam("image") MultipartFile image, HttpSession session){
        try {
            Response response = studentService.register(student, image, session);
            return ResponseEntity.ok().body(response);
        } catch (UnhandledRejection e) {
            return ResponseEntity.badRequest().body(new Response(e.getMessage()) {
            });
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login, HttpSession session){
        try {
            Response response = studentService.login(login, session);
            return ResponseEntity.ok().body(response);
        } catch (UnhandledRejection e) {
            return ResponseEntity.badRequest().body(new Response(e.getMessage()) {
            });
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(e.getMessage()));
        }
    }
}
