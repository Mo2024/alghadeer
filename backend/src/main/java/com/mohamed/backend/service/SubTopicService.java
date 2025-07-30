package com.mohamed.backend.service;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.HandledRejection;
import com.mohamed.backend.model.topics.SubTopic;
import com.mohamed.backend.repository.topic.MainTopicRepository;
import com.mohamed.backend.repository.topic.SubTopicRepository;
import com.mohamed.backend.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SubTopicService {

    @Autowired
    private MainTopicRepository mainTopicRepository;

    @Autowired
    private SubTopicRepository subTopicRepository;


    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public SubTopic createSubTopic(SubTopic subTopic) {
        log.info("executing method [TopicService].[createSubTopic]");

        log.info("Request parameter details:\n{}",subTopic);

        if (subTopic.getName() == null || subTopic.getName().trim().isEmpty() || !ValidationUtils.isArabic(subTopic.getName())) {
            log.error("Invalid name:\n{}", subTopic.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        mainTopicRepository.findById(subTopic.getMainTopic().getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });

        SubTopic  subTopic1 = subTopicRepository.save(subTopic);

        log.info("[TopicService].[createSubTopic] executed successfully");
        return subTopic1;
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response editSubTopic(SubTopic subTopic) {
        log.info("executing method [TopicService].[editSubTopic]");

        log.info("Request parameter details:\n{}",subTopic);

        if (subTopic.getName() == null || subTopic.getName().trim().isEmpty() || !ValidationUtils.isArabic(subTopic.getName())) {
            log.error("Invalid name:\n{}", subTopic.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        subTopicRepository.findById(subTopic.getId())
                .orElseThrow(() -> {
                    log.error("Sub topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الفرعي");
                });
        mainTopicRepository.findById(subTopic.getMainTopic().getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });

        subTopicRepository.save(subTopic);

        log.info("[TopicService].[editSubTopic] executed successfully");
        return new Response("تم تعديل الموضوع الفرعي بنجاح");
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response deleteSubTopic(SubTopic subTopic) {
        log.info("executing method [TopicService].[deleteSubTopic]");

        log.info("Request parameter details:\n{}",subTopic);

        subTopicRepository.findById(subTopic.getId())
                .orElseThrow(() -> {
                    log.error("Sub topic not found");
                    return new HandledRejection("الرجاء التحقق من وجود الموضوع الفرعي");
                });

        subTopicRepository.delete(subTopic);

        log.info("[TopicService].[deleteSubTopic] executed successfully");
        return new Response("تم حذف الموضوع الفرعي بنجاح");
    }
}
