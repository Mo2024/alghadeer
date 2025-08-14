package com.mohamed.backend.controller;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.service.AuthenticationService;
import com.mohamed.backend.utils.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management", description = "Operations related to authenticating user")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final Logger logger;

    @GetMapping("/get-auth")
    @Operation(
            summary = "Verifies that the user is authenticated before rendering a page/proceeding with an action"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> getStudentAuthentication() {
        try {
            log.info("executing method [authenticationService].[getAuthentication]");
            Map<?, Boolean> response = authenticationService.getAuthentication();
            log.info("[authenticationService].[getAuthentication] executed successfully");
            logger.logJsonObject("Response for [getAuthentication]:\n{}", response);
            return ResponseEntity.ok().body(response);
        } catch (AuthorizationDeniedException e) {
            log.error("Authorization Denied error:", e);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new Response("ليس لديك صلاحية للوصول إلى هذا المورد", "ALGD-403"));
        } catch (Exception e) {
            log.error("Unexpected error:", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", "ALGD-500"));
        }
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logs user out",
            description = "This request is in the student controller/service but it is for staff as well"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            log.info("executing method [authenticationService].[logout]");
            Response response = authenticationService.logout(session);
            log.info("[authenticationService].[logout] executed successfully");
            logger.logJsonObject("Response for [logout]:\n{}", response);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Unexpected error:", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", "ALGD-500"));
        }
    }

}
