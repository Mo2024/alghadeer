package com.mohamed.backend.controller;

import com.mohamed.backend.dto.*;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.enums.Grade;
import com.mohamed.backend.model.semester.Semester;
import com.mohamed.backend.service.SemesterService;
import com.mohamed.backend.utils.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/semester")
@Tag(name = "Semester Management", description = "Operations related to semester lifecycle and student enrollment")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private Logger logger;

    @PostMapping("/admin/create")
    @Operation(
            summary = "Creates a semester",
            description = """
                    This request performs a lot of operations including:
                     \
                    1) Semester Creation
                    2) Classes Creation
                    3) Class Schedule Creation
                    4) Session Creation
                    5) Grade to class mapping/creation (Ex. putting 1st and 2nd grade in one class)
                    6) Assigns a staff/instructor for a single class and sessions (one to many relationship)
                    
                    Only admins are authorized to perform this request."""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> createSemester(@RequestBody SemesterDto semester) {
        try {
            log.info("executing method [semesterService].[createSemester]");
            Response response = semesterService.createSemester(semester);
            log.info("[semesterService].[createSemester] executed successfully");
            logger.logJsonObject("Response for [createSemester]:\n{}", response);
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

    @PutMapping("/admin/close-semester")
    @Operation(
            summary = "Closes the current active semester",
            description = "Only admins are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> closeActiveSemester(@RequestParam int page, @RequestParam int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            log.info("executing method [semesterService].[closeActiveSemester]");
            Page<SemesterView> response = semesterService.closeActiveSemester(pageable);
            log.info("[semesterService].[closeActiveSemester] executed successfully");
            logger.logJsonObject("Response for [closeActiveSemester]:\n{}", response);
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

    @PostMapping("/student/enroll")
    @Operation(
            summary = "Enrolls the students in the semester and assigns them to a class based on their grade",
            description = "Only students are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> enrollSemester(@RequestBody String grade) {
        try {
            log.info("executing method [semesterService].[enrollSemester]");
            Response response = semesterService.enrollSemester(Grade.valueOf(grade));
            log.info("[semesterService].[enrollSemester] executed successfully");
            logger.logJsonObject("Response for [enrollSemester]:\n{}", response);
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

    @GetMapping("/student/is-enrolled")
    @Operation(
            summary = "Checks if students are enrolled or not",
            description = "Only students are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> isEnrolled() {
        try {
            log.info("executing method [semesterService].[isEnrolled]");
            boolean response = semesterService.isEnrolled();
            log.info("[semesterService].[isEnrolled] executed successfully");
            logger.logJsonObject("Response for [isEnrolled]:\n{}", response);
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


    @GetMapping("/admin/get-semesters")
    @Operation(
            summary = "Archives a staff account (soft delete)",
            description = "Only admins are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> getSemesters(@RequestParam int page, @RequestParam int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            log.info("executing method [semesterService].[getSemesters]");
            Page<SemesterView> response = semesterService.getSemesters(pageable);
            log.info("[semesterService].[getSemesters] executed successfully");
            logger.logJsonObject("Response for [getSemesters]:\n{}", response);
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

}
