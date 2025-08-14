package com.mohamed.backend.controller;

import com.mohamed.backend.dto.*;
import com.mohamed.backend.dto.user.ArchiveDto;
import com.mohamed.backend.dto.user.Login;
import com.mohamed.backend.dto.user.StaffListView;
import com.mohamed.backend.dto.user.StaffView;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.user.Staff;
import com.mohamed.backend.service.StaffService;
import com.mohamed.backend.utils.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Tag(name = "Staff Management", description = "Operations related to staff entity")
public class StaffController {

    private final StaffService staffService;
    private final Logger logger;

    @PostMapping("/admin/register")
    @Operation(
            summary = "Creates a new staff",
            description = "Only admins are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> register(@RequestBody Staff staff) {
        try {
            log.info("executing method [staffService].[register]");
            Response response = staffService.register(staff);
            log.info("[staffService].[register] executed successfully");
            logger.logJsonObject("Response for [register]:\n{}", response);
            return ResponseEntity.ok().body(response);
        } catch (HandledRejection e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(e.getMessage(), "ALGD-400"));
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

    @PostMapping("/login")
    @Operation(
            summary = "Login request for staff",
            description = "This request will only check for staff, students do not use this route."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> login(@RequestBody Login login, HttpSession session) {
        try {
            log.info("executing method [staffService].[login]");
            Response response = staffService.login(login, session);
            log.info("[staffService].[login] executed successfully");
            logger.logJsonObject("Response for [login]:\n{}", response);
            return ResponseEntity.ok().body(response);
        } catch (HandledRejection e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(e.getMessage(), "ALGD-400"));
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

    @PutMapping("/admin/archive")
    @Operation(
            summary = "Archives a staff account (soft delete)",
            description = "Only admins are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> archiveStaff(@RequestBody ArchiveDto archiveDto) {
        try {
            log.info("executing method [staffService].[archiveStaff]");
            Page<StaffView> response = staffService.archiveStaff(archiveDto);
            log.info("[staffService].[archiveStaff] executed successfully");
            logger.logJsonObject("Response for [archiveStaff]:\n{}", response);
            return ResponseEntity.ok().body(response);
        } catch (HandledRejection e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(e.getMessage(), "ALGD-400"));
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

    @GetMapping("/admin/get-staff")
    @Operation(
            summary = "Archives a staff account (soft delete)",
            description = "Only admins are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> getStaff(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        try {
            if (page != null && size != null) {
                Pageable pageable = PageRequest.of(page, size);
                log.info("executing method [staffService].[getStaff]");
                Page<StaffView> response = staffService.getStaff(pageable);
                log.info("[staffService].[getStaff] executed successfully");
                logger.logJsonObject("Response for [getStaff]:\n{}", response);
                return ResponseEntity.ok().body(response);
            } else {
                List<StaffListView> allStaff = staffService.getStaff();
                return ResponseEntity.ok().body(allStaff);
            }
        } catch (HandledRejection e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(e.getMessage(), "ALGD-400"));
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
}
