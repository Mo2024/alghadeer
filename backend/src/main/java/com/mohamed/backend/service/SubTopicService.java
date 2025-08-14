package com.mohamed.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.topics.MainTopic;
import com.mohamed.backend.model.topics.SubTopic;
import com.mohamed.backend.repository.topic.MainTopicRepository;
import com.mohamed.backend.repository.topic.SubTopicRepository;
import com.mohamed.backend.utils.Logger;
import com.mohamed.backend.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class SubTopicService {

    private final MainTopicRepository mainTopicRepository;
    private final SubTopicRepository subTopicRepository;
    private final MainTopicService mainTopicService;
    private final StaffService staffService;
    private final Logger logger;

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<MainTopic> createSubTopic(SubTopic subTopic) throws JsonProcessingException {
        log.info("Staff ID:{}", staffService.getStaffId());

        logger.logJsonObject("Request parameter details:\n{}", subTopic);

        if (subTopic.getName() == null || subTopic.getName().trim().isEmpty() || !ValidationUtils.isArabic(subTopic.getName())) {
            log.error("Invalid name:\n{}", subTopic.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        log.info("Calling [mainTopicRepository].[findById]");
        mainTopicRepository.findById(subTopic.getMainTopic().getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });
        log.info("[mainTopicRepository].[findById] called successfully");

        log.info("Calling [subTopicRepository].[save]");
        subTopic = subTopicRepository.save(subTopic);
        log.info("[subTopicRepository].[save] called successfully");

        logger.logJsonObject("Subtopic created successfully:\n{}", subTopic);

        return mainTopicService.getTopics();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<MainTopic> editSubTopic(SubTopic subTopic) throws JsonProcessingException {
        log.info("Staff ID:{}", staffService.getStaffId());

        logger.logJsonObject("Request parameter details:\n{}", subTopic);

        if (subTopic.getName() == null || subTopic.getName().trim().isEmpty() || !ValidationUtils.isArabic(subTopic.getName())) {
            log.error("Invalid name:\n{}", subTopic.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        log.info("Calling [subTopicRepository].[findById]");
        subTopicRepository.findById(subTopic.getId())
                .orElseThrow(() -> {
                    log.error("Sub topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الفرعي");
                });
        log.info("[subTopicRepository].[findById] called successfully");

        log.info("Calling [mainTopicRepository].[findById]");
        mainTopicRepository.findById(subTopic.getMainTopic().getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });
        log.info("[mainTopicRepository].[findById] called successfully");

        log.info("Calling [subTopicRepository].[save]");
        subTopic = subTopicRepository.save(subTopic);
        log.info("[subTopicRepository].[save] called successfully");

        logger.logJsonObject("Subtopic edited successfully:\n{}", subTopic);

        return mainTopicService.getTopics();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response deleteSubTopic(SubTopic subTopic) throws JsonProcessingException {
        log.info("Staff ID:{}", staffService.getStaffId());

        logger.logJsonObject("Request parameter details:\n{}", subTopic);

        log.info("Calling [subTopicRepository].[findById]");
        subTopicRepository.findById(subTopic.getId())
                .orElseThrow(() -> {
                    log.error("Sub topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الفرعي");
                });
        log.info("[subTopicRepository].[findById] called successfully");

        log.info("Calling [subTopicRepository].[delete]");
        subTopicRepository.delete(subTopic);
        log.info("[subTopicRepository].[delete] called successfully");

        return new Response("تم حذف الموضوع الفرعي بنجاح");
    }
}
