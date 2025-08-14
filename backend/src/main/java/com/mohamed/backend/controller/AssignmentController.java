package com.mohamed.backend.controller;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.classinfo.assignment.Assignment;
import com.mohamed.backend.service.AssignmentService;
import com.mohamed.backend.utils.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/assignment")
@RequiredArgsConstructor
@Tag(name = "Assignment Management", description = "Operations related to students/class assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final Logger logger;

    @PostMapping("/all/create-assignment")
    @Operation(
            summary = "creates an assignment to a class",
            description = "Onl instructors assigned to the class, supervisor and admin are authorized"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> getStudentAuthentication(@RequestBody Assignment assignment, @RequestParam int classId) {
        try {
            log.info("executing method [assignmentService].[createAssignment]");
            Assignment response = assignmentService.createAssignment(assignment, classId);
            log.info("[assignmentService].[createAssignment] executed successfully");
            logger.logJsonObject("Response for [createAssignment]:\n{}", response);
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
