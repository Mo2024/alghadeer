package com.mohamed.backend.salah.attempt;

import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestion;
import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestionService;
import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestionView;
import com.mohamed.backend.salah.level.StudentLevel;
import com.mohamed.backend.salah.questions.subjects.Subject;
import com.mohamed.backend.utils.Response;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.utils.methods.Logger;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/salah/attempt")
@RequiredArgsConstructor
@Tag(name = "Salah Attempt Management", description = "Operations related to Salah test attempt entity for salah module")
public class AttemptController {

    private final AttemptService attemptService;
    private final StudentSalahQuestionService studentSalahQuestionService;
    private final Logger logger;

    @PostMapping("/all/create-attempt")
    @Operation(
            summary = "Creates salah attempt",
            description = "Only staff are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> createAttempt(@RequestBody List<Integer> selectedSubjects, @RequestParam int studentId) {
        try {
            log.info("executing method [attemptService].[createSalahAttempt]");
            List<StudentSalahQuestionView> response = attemptService.createSalahAttempt(selectedSubjects, studentId);
            log.info("[attemptService].[createSalahAttempt] executed successfully");
            logger.logJsonObject("Response for [createSalahAttempt]:\n{}", response);
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

    @GetMapping("/all/latest-attempt")
    @Operation(
            summary = "Fetches latest attempt for all subjects the student took",
            description = "Only staff are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> getLatestAttempt(@RequestParam int studentId) {
        try {
            log.info("executing method [attemptService].[getLatestAttempts]");
            List<SalahAttemptView> response = attemptService.getLatestAttempts(studentId);
            log.info("[attemptService].[getLatestAttempts] executed successfully");
            logger.logJsonObject("Response for [getLatestAttempts]:\n{}", response);
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

    @GetMapping("/all/attempt-questions")
    @Operation(
            summary = "Fetches questions of attempt",
            description = "Only staff are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> getAttemptQuestions(@RequestParam int attemptId) {
        try {
            log.info("executing method [studentSalahQuestionService].[getQuestionsOfAttempt]");
            List<StudentSalahQuestionView> response = studentSalahQuestionService.getQuestionsOfAttempt(attemptId);
            log.info("[studentSalahQuestionService].[getQuestionsOfAttempt] executed successfully");
            logger.logJsonObject("Response for [getQuestionsOfAttempt]:\n{}", response);
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

    @PutMapping("/all/save-questions")
    @Operation(
            summary = "Saves questions of an attempt",
            description = "Only staff are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> SaveQuestions(@RequestBody List<StudentSalahQuestion> questionList, @RequestParam int attemptId) {
        try {
            log.info("executing method [attemptService].[createSalahAttempt]");
            Response response = studentSalahQuestionService.saveAttempt(questionList, attemptId);
            log.info("[attemptService].[createSalahAttempt] executed successfully");
            logger.logJsonObject("Response for [createSalahAttempt]:\n{}", response);
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
