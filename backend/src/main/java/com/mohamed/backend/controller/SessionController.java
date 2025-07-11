package com.mohamed.backend.controller;

import com.mohamed.backend.dto.AttendanceRequestDTO;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.user.Student;
import com.mohamed.backend.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/instructor/take-attendance")
    public ResponseEntity<?> takeAttendance(@RequestBody AttendanceRequestDTO attendanceRequest){
        try {
            Response response = attendanceService.takeAttendance(attendanceRequest);
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
