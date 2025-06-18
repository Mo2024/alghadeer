package com.mohamed.backend.controller;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.Staff;
import com.mohamed.backend.model.Student;
import com.mohamed.backend.service.StaffService;
import com.mohamed.backend.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/staff")
public class StaffController {


    @Autowired
    private StaffService staffService;

    @PostMapping("/admin/register")
    public ResponseEntity<?> register(@RequestBody Staff staff, HttpSession session){
        try {
            Response response = staffService.register(staff);
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
