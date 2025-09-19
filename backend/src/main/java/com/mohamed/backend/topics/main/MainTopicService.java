package com.mohamed.backend.topics.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.utils.Response;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.utils.methods.Logger;
import com.mohamed.backend.utils.methods.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MainTopicService {

    private final MainTopicRepository mainTopicRepository;
    private final Logger logger;

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

        log.info("Calling [mainTopicRepository].[save]");
        mainTopicRepository.save(mainTopic);
        log.info("[mainTopicRepository].[save] called successfully");

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

        log.info("Calling [mainTopicRepository].[findById]");
        mainTopicRepository.findById(mainTopic.getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });
        log.info("[mainTopicRepository].[findById] called successfully");

        log.info("Calling [mainTopicRepository].[save]");
        mainTopicRepository.save(mainTopic);
        log.info("[mainTopicRepository].[save] called successfully");

        return getTopics();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response deleteMainTopic(MainTopic mainTopic) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", mainTopic);

        log.info("Calling [mainTopicRepository].[findById]");
        mainTopicRepository.findById(mainTopic.getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });
        log.info("[mainTopicRepository].[findById] called successfully");

        log.info("Calling [mainTopicRepository].[delete]");
        mainTopicRepository.delete(mainTopic);
        log.info("[mainTopicRepository].[delete] called successfully");

        return new Response("تم حذف الموضوع الرئيسي بنجاح");
    }
}
