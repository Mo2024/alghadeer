package com.mohamed.backend.topics.sub;


import com.mohamed.backend.utils.Response;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.topics.main.MainTopic;
import com.mohamed.backend.utils.methods.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;
import org.postgresql.util.PSQLException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/sub-topics")
@RequiredArgsConstructor
@Tag(name = "Sub-topic Management", description = "Operations related to sub-topic entity")
public class SubTopicController {

    private final SubTopicService subTopicService;
    private final Logger logger;

    @PostMapping("/all/create-sub-topic")
    @Operation(
            summary = "Creates a sub topic",
            description = "Only staff are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> createSubTopic(@RequestBody SubTopic subTopic) {
        try {
            log.info("executing method [TopicService].[createSubTopic]");
            List<MainTopic> response = subTopicService.createSubTopic(subTopic);
            log.info("[TopicService].[createSubTopic] executed successfully");
            logger.logJsonObject("Response for [createSubTopic]:\n{}", response);
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

    @PutMapping("/all/edit-sub-topic")
    @Operation(
            summary = "Edits an existing sub topic",
            description = "Only staff are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> editSubTopic(@RequestBody SubTopic subTopic) {
        try {
            log.info("executing method [TopicService].[editSubTopic]");
            List<MainTopic> response = subTopicService.editSubTopic(subTopic);
            log.info("[TopicService].[editSubTopic] executed successfully");
            logger.logJsonObject("Response for [editSubTopic]:\n{}", response);
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

    @DeleteMapping("/all/delete-sub-topic")
    @Operation(
            summary = "Deletes a sub topic",
            description = "Only staff are authorized to perform this request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success request - Request executed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Handled rejection in service"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authorization denied (does not have the required role)"),
            @ApiResponse(responseCode = "500", description = "Internal server error - Usually an unhandled rejection")
    })
    public ResponseEntity<?> deleteSubTopic(@RequestBody SubTopic subTopic) throws Exception {
        try {
            log.info("executing method [TopicService].[deleteSubTopic]");
            Response response = subTopicService.deleteSubTopic(subTopic);
            log.info("[TopicService].[deleteSubTopic] executed successfully");
            logger.logJsonObject("Response for [deleteSubTopic]:\n{}", response);
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
        } catch (DataIntegrityViolationException ex) {
            Throwable cause = ex.getCause();
            while (cause != null) {
                if (cause instanceof PSQLException pgException) {
                    if (pgException.getSQLState().equals("23503")) {
                        return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(new Response("لا يمكن حذف الموضوع لأنه مرتبط حصة", "ALGD-409"));
                    }
                }
                cause = cause.getCause();
            }
            throw new Exception();
        } catch (Exception e) {
            log.error("Unexpected error:", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("حدث خطأ غير متوقع، يرجى التواصل مع إشراف التعليم الديني", "ALGD-500"));
        }
    }

}
