package com.mohamed.backend.service;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.topics.MainTopic;
import com.mohamed.backend.repository.topic.MainTopicRepository;
import com.mohamed.backend.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MainTopicService {

    @Autowired
    private MainTopicRepository mainTopicRepository;


    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<MainTopic> getTopics() {
        log.info("executing method [TopicService].[getTopics]");

        List<MainTopic> topics =  mainTopicRepository.findAll();

        log.info("[TopicService].[getTopics] executed successfully");
        return topics;
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response createMainTopic(MainTopic mainTopic) {
        log.info("executing method [TopicService].[createMainTopic]");

        log.info("Request parameter details:\n{}",mainTopic);

        if (mainTopic.getName() == null || mainTopic.getName().trim().isEmpty() || !ValidationUtils.isArabic(mainTopic.getName())) {
            log.error("Invalid name:\n{}", mainTopic.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        mainTopicRepository.save(mainTopic);

        log.info("[TopicService].[createMainTopic] executed successfully");
        return new Response("تم إنشاء الموضوع الرئيسي بنجاح");
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response editMainTopic(MainTopic mainTopic) {
        log.info("executing method [TopicService].[editMainTopic]");

        log.info("Request parameter details:\n{}",mainTopic);

        if (mainTopic.getName() == null || mainTopic.getName().trim().isEmpty() || !ValidationUtils.isArabic(mainTopic.getName())) {
            log.error("Invalid name:\n{}", mainTopic.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        mainTopicRepository.findById(mainTopic.getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });

        mainTopicRepository.save(mainTopic);

        log.info("[TopicService].[editMainTopic] executed successfully");
        return new Response("تم تعديل الموضوع الرئيسي بنجاح");
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response deleteMainTopic(MainTopic mainTopic) {
        log.info("executing method [TopicService].[deleteMainTopic]");

        log.info("Request parameter details:\n{}",mainTopic);

        mainTopicRepository.findById(mainTopic.getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });

        mainTopicRepository.delete(mainTopic);

        log.info("[TopicService].[deleteMainTopic] executed successfully");
        return new Response("تم حذف الموضوع الرئيسي بنجاح");
    }
}
