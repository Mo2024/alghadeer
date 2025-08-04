package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.topics.MainTopic;
import com.mohamed.backend.repository.topic.MainTopicRepository;
import com.mohamed.backend.utils.Logger;
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

    @Autowired
    private Logger logger;

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<MainTopic> getTopics() {
        return mainTopicRepository.findAllByOrderByIdAsc();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<MainTopic> createMainTopic(MainTopic mainTopic) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", mainTopic);

        if (mainTopic.getName() == null || mainTopic.getName().trim().isEmpty() || !ValidationUtils.isArabic(mainTopic.getName())) {
            log.error("Invalid name:\n{}", mainTopic.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        mainTopicRepository.save(mainTopic);

        return getTopics();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<MainTopic> editMainTopic(MainTopic mainTopic) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", mainTopic);

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

        return getTopics();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response deleteMainTopic(MainTopic mainTopic) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", mainTopic);

        mainTopicRepository.findById(mainTopic.getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });

        mainTopicRepository.delete(mainTopic);

        return new Response("تم حذف الموضوع الرئيسي بنجاح");
    }
}
