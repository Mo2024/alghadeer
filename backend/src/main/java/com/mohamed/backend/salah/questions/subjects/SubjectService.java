package com.mohamed.backend.salah.questions.subjects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.utils.methods.Logger;
import com.mohamed.backend.utils.methods.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectAreaRepository subjectAreaRepository;
    private final Logger logger;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    public List<Subject> getSubjects() {
        return subjectRepository.findAll();
    }


    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    public List<Subject> createSubject(Subject subject) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", subject);

        if (subject.getName() == null || subject.getName().trim().isEmpty() || !ValidationUtils.isArabic(subject.getName())) {
            log.error("Invalid subject name:\n{}", subject.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الموضوع بشكل صحيح وباللغة العربية");
        }

        List<SubjectArea> subjectAreas = subject.getSubjectAreas();
        for (SubjectArea subjectArea : subjectAreas) {
            if (subjectArea.getName() == null || subjectArea.getName().trim().isEmpty() || !ValidationUtils.isArabic(subjectArea.getName())) {
                log.error("Invalid subject area name:\n{}", subjectArea.getName());
                throw new HandledRejection("يرجى التأكد من إدخال المحور بشكل صحيح وباللغة العربية");
            }
            subjectArea.setSubject(subject);
        }

        log.info("Calling [subjectRepository].[save]");
        subjectRepository.save(subject);
        log.info("[subjectRepository].[save] called successfully");

        return subjectRepository.findAll();
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    public SubjectArea createSubjectArea(SubjectArea subjectArea) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", subjectArea);

        if (subjectArea.getName() == null || subjectArea.getName().trim().isEmpty() || !ValidationUtils.isArabic(subjectArea.getName())) {
            log.error("Invalid subject area name:\n{}", subjectArea.getName());
            throw new HandledRejection("يرجى التأكد من إدخال المحور بشكل صحيح وباللغة العربية");
        }

        log.info("Calling [subjectRepository].[findById]");
        subjectRepository.findById(subjectArea.getSubject().getId())
                .orElseThrow(() -> {
                    log.error("Subject not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[subjectRepository].[findById] called successfully");

        log.info("Calling [subjectAreaRepository].[save]");
        subjectAreaRepository.save(subjectArea);
        log.info("[subjectAreaRepository].[save] called successfully");

        return subjectArea;
    }


    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    public List<Subject> editSubject(Subject subjectReq) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", subjectReq);


        log.info("Calling [subjectRepository].[findById]");
        Subject subject = subjectRepository.findById(subjectReq.getId())
                .orElseThrow(() -> {
                    log.error("Subject not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[subjectRepository].[findById] called successfully");


        if (subject.getName() == null || subject.getName().trim().isEmpty() || !ValidationUtils.isArabic(subject.getName())) {
            log.error("Invalid subject name:\n{}", subject.getName());
            throw new HandledRejection("يرجى التأكد من إدخال الموضوع بشكل صحيح وباللغة العربية");
        }

        subject.setName(subjectReq.getName());

        log.info("Calling [subjectRepository].[save]");
        subjectRepository.save(subject);
        log.info("[subjectRepository].[save] called successfully");

        return subjectRepository.findAll();
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    public SubjectArea editSubjectArea(SubjectArea subjectAreaReq) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", subjectAreaReq);

        log.info("Calling [subjectRepository].[findById]");
        subjectRepository.findById(subjectAreaReq.getSubject().getId())
                .orElseThrow(() -> {
                    log.error("Subject not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[subjectRepository].[findById] called successfully");


        log.info("Calling [subjectAreaRepository].[findByIdAndSubjectId]");
        SubjectArea subjectArea = subjectAreaRepository.findByIdAndSubjectId(subjectAreaReq.getId(), subjectAreaReq.getSubject().getId())
                .orElseThrow(() -> {
                    log.error("Subject area not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[subjectAreaRepository].[findByIdAndSubjectId] called successfully");


        if (subjectArea.getName() == null || subjectArea.getName().trim().isEmpty() || !ValidationUtils.isArabic(subjectArea.getName())) {
            log.error("Invalid subject area name:\n{}", subjectArea.getName());
            throw new HandledRejection("يرجى التأكد من إدخال المحور بشكل صحيح وباللغة العربية");
        }

        subjectArea.setName(subjectAreaReq.getName());

        log.info("Calling [subjectAreaRepository].[save]");
        subjectAreaRepository.save(subjectArea);
        log.info("[subjectAreaRepository].[save] called successfully");

        return subjectArea;
    }
}
