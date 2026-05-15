package com.mohamed.backend.topics.group;

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
public class TopicGroupService {

    private final TopicGroupRepository topicGroupRepository;
    private final Logger logger;

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<TopicGroup> getTopicsGroups() {
        return topicGroupRepository.findAllByArchivedFalseOrderByIdAsc();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<TopicGroup> createTopicGroup(TopicGroup topicGroup) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", topicGroup);

        if (topicGroup.getName() == null || topicGroup.getName().trim().isEmpty() || !ValidationUtils.isArabic(topicGroup.getName())) {
            log.error("Invalid name:\n{}", topicGroup.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        topicGroup.setArchived(false);

        log.info("Calling [topicGroupRepository].[save]");
        topicGroupRepository.save(topicGroup);
        log.info("[topicGroupRepository].[save] called successfully");

        return getTopicsGroups();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public List<TopicGroup> editTopicGroup(TopicGroup topicGroup) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", topicGroup);

        if (topicGroup.getName() == null || topicGroup.getName().trim().isEmpty() || !ValidationUtils.isArabic(topicGroup.getName())) {
            log.error("Invalid name:\n{}", topicGroup.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        log.info("Calling [topicGroupRepository].[findById]");
        topicGroupRepository.findById(topicGroup.getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });
        log.info("[topicGroupRepository].[findById] called successfully");

        topicGroup.setArchived(false);

        log.info("Calling [topicGroupRepository].[save]");
        topicGroupRepository.save(topicGroup);
        log.info("[topicGroupRepository].[save] called successfully");

        return getTopicsGroups();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response deleteTopicGroup(TopicGroup topicGroup) throws JsonProcessingException {

        logger.logJsonObject("Request parameter:\n{}", topicGroup);

        log.info("Calling [topicGroupRepository].[findById]");
        topicGroupRepository.findById(topicGroup.getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });
        log.info("[topicGroupRepository].[findById] called successfully");

        log.info("Calling [topicGroupRepository].[delete]");
        topicGroupRepository.delete(topicGroup);
        log.info("[topicGroupRepository].[delete] called successfully");

        return new Response("تم حذف مجموعة المواضيع بنجاح");
    }
}
