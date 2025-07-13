package com.mohamed.backend.controller;


import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.topics.SubTopic;
import com.mohamed.backend.service.SubTopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/sub-topics")
public class SubTopicController {

    @Autowired
    private SubTopicService subTopicService;


    @PostMapping("/staff/create-sub-topic")
    public ResponseEntity<?> createSubTopic(@RequestBody SubTopic subTopic){
        try {
            Response response = subTopicService.createSubTopic(subTopic);
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

    @PostMapping("/staff/edit-sub-topic")
    public ResponseEntity<?> editSubTopic(@RequestBody SubTopic subTopic){
        try {
            Response response = subTopicService.editSubTopic(subTopic);
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

    @PostMapping("/staff/delete-sub-topic")
    public ResponseEntity<?> deleteSubTopic(@RequestBody SubTopic subTopic){
        try {
            Response response = subTopicService.deleteSubTopic(subTopic);
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
