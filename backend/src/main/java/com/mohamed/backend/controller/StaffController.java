package com.mohamed.backend.controller;

import com.mohamed.backend.dto.Login;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.user.Staff;
import com.mohamed.backend.service.StaffService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/staff")
public class StaffController {


    @Autowired
    private StaffService staffService;

    @PostMapping("/admin/register")
    public ResponseEntity<?> register(@RequestBody Staff staff){
        try {
            Response response = staffService.register(staff);
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login, HttpSession session){
        try {
            Response response = staffService.login(login, session);
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
