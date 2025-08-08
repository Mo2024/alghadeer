package com.mohamed.backend.controller;

import com.mohamed.backend.dto.*;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.service.AttendanceService;
import com.mohamed.backend.service.SessionService;
import com.mohamed.backend.utils.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/session")
@Tag(name = "Session Management", description = "Operations related to session entity")
public class SessionController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private Logger logger;

    @PostMapping("/all/take-attendance")
    @Operation(
            summary = "Takes attendance of student for a given session",
            description = "Only admins/instructors/staff who is assigned to the class or session is authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> takeAttendance(@RequestBody AttendanceRequestDTO attendanceRequest) {
        try {
            log.info("executing method [attendanceService].[takeAttendance]");
            GetAttendanceStatusDto response = attendanceService.takeAttendance(attendanceRequest);
            log.info("[attendanceService].[takeAttendance] executed successfully");
            logger.logJsonObject("Response for [takeAttendance]:\n{}", response);
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

    @PostMapping("/all/cancel-sessions")
    @Operation(
            summary = "Cancels a sessions in bulk (or individually too)",
            description = "Only admins/instructors/staff who is assigned to the class or session is authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> cancelSessions(@RequestBody List<Integer> sessionIds) {
        try {
            log.info("executing method [sessionService].[cancelSessions]");
            Response response = sessionService.cancelSessions(sessionIds);
            log.info("[sessionService].[cancelSessions] executed successfully");
            logger.logJsonObject("Response for [cancelSessions]:\n{}", response);
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

    @PutMapping("/all/change-sub-topic")
    @Operation(
            summary = "Changes a sub-topic of a session",
            description = "Only admins/instructors/staff who is assigned to the class or session is authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> changeSubTopic(@RequestBody AddSubTopicDto addSubTopicDto) {
        try {
            log.info("executing method [sessionService].[changeSubTopic]");
            Response response = sessionService.changeSubTopic(addSubTopicDto);
            log.info("[sessionService].[changeSubTopic] executed successfully");
            logger.logJsonObject("Response for [changeSubTopic]:\n{}", response);
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

    @GetMapping("/all/upcoming-sessions")
    @Operation(
            summary = "Fetches upcoming sessions for staff",
            description = "Only staff are unauthorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> getAssignedClasses(@RequestParam int page, @RequestParam int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            log.info("executing method [sessionService].[getUpcomingSessions]");
            Page<SessionView> response = sessionService.getUpcomingSessions(pageable);
            log.info("[sessionService].[getUpcomingSessions] executed successfully");
            logger.logJsonObject("Response for [getUpcomingSessions]:\n{}", response);
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

    @GetMapping("/all/get-attendance-status")
    @Operation(
            summary = "This fetches if the attendance have been taken and the data needed depending on if the attendance have been taken",
            description = "Only staff are unauthorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> getAttendanceStatus(@RequestParam int sessionId, @RequestParam int classId) {
        try {
            log.info("executing method [attendanceService].[getAttendanceStatus]");
            GetAttendanceStatusDto response = attendanceService.getAttendanceStatus(sessionId, classId);
            log.info("[attendanceService].[getAttendanceStatus] executed successfully");
            logger.logJsonObject("Response for [getAttendanceStatus]:\n{}", response);
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
