package com.mohamed.backend.controller;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.Semester;
import com.mohamed.backend.service.SemesterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/semester")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    @PostMapping("/admin/create")
    public ResponseEntity<?> createSemester(@RequestBody Semester semester){
        try {
            Response response = semesterService.createSemester(semester);
            return ResponseEntity.ok().body(response);
        } catch (UnhandledRejection e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(e.getMessage()));
        } catch (AuthorizationDeniedException e) {
            log.error("Authorization Denied error:", e);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new Response("ليس لديك صلاحية للوصول إلى هذا المورد"));
        } catch (Exception e) {
            log.error("Unexpected error:", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني"));
        }
    }

    @PutMapping("/admin/{semesterId}/toggle-status")
    public ResponseEntity<?> switchSemesterStatus(@PathVariable Integer semesterId) {
        try {
            Response response = semesterService.switchSemesterStatus(semesterId);
            return ResponseEntity.ok().body(response);
        } catch (UnhandledRejection e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(e.getMessage()));
        } catch (AuthorizationDeniedException e) {
            log.error("Authorization Denied error:", e);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new Response("ليس لديك صلاحية للوصول إلى هذا المورد"));
        } catch (Exception e) {
            log.error("Unexpected error:", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني"));
        }
    }

    @PostMapping("/student/enroll")
    public ResponseEntity<?> enrollSemester(){
        try {
            Response response = semesterService.enrollSemester();
            return ResponseEntity.ok().body(response);
        } catch (UnhandledRejection e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(e.getMessage()));
        } catch (AuthorizationDeniedException e) {
            log.error("Authorization Denied error:", e);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new Response("ليس لديك صلاحية للوصول إلى هذا المورد"));
        } catch (Exception e) {
            log.error("Unexpected error:", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني"));
        }
    }

}
