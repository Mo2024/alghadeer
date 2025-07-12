package com.mohamed.backend.service;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.classinfo.Session;
import com.mohamed.backend.model.topics.MainTopic;
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
public class TopicService {

    @Autowired
    private MainTopicRepository mainTopicRepository;

    @Autowired
    private SubTopicRepository subTopicRepository;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response createMainTopic(MainTopic mainTopic) {
        log.info("executing method [TopicService].[createMainTopic]");

        log.info("Request parameter details:\n{}",mainTopic);

        if (mainTopic.getName() == null || mainTopic.getName().trim().isEmpty() || !ValidationUtils.isArabic(mainTopic.getName())) {
            log.error("Invalid name:\n{}", mainTopic.getName());
            throw new UnhandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        mainTopicRepository.save(mainTopic);

        log.info("[TopicService].[createMainTopic] executed successfully");
        return new Response("تم إنشاء الموضوع الرئيسي بنجاح");
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response createSubTopic(SubTopic subTopic) {
        log.info("executing method [TopicService].[createSubTopic]");

        log.info("Request parameter details:\n{}",subTopic);

        if (subTopic.getName() == null || subTopic.getName().trim().isEmpty() || !ValidationUtils.isArabic(subTopic.getName())) {
            log.error("Invalid name:\n{}", subTopic.getName());
            throw new UnhandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        mainTopicRepository.findById(subTopic.getMainTopic().getId())
                .orElseThrow(() -> {
                    log.error("Main topic not found");
                    return new UnhandledRejection("الرجاء التحقق من وجود الموضوع الرئيسي");
                });

        subTopicRepository.save(subTopic);

        log.info("[TopicService].[createSubTopic] executed successfully");
        return new Response("تم إنشاء الموضوع الرئيسي بنجاح");
    }
}
