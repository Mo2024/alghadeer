package com.mohamed.backend.salah.questions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.utils.methods.Logger;
import com.mohamed.backend.utils.methods.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    @PersistenceContext
    private EntityManager entityManager;
    private final QuestionRepository questionRepository;
    private final Logger logger;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR','INSTRUCTOR')")
    public List<Question> getQuestionsByLevel(Level level) {
        log.info("Level param: {}", level);
        return questionRepository.findAllByLevelAndDeletedFalse(level);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    public Page<Question> getQuestionsAdmin(Pageable pageable) {
        return questionRepository.findAllByDeletedFalse(pageable);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    @Transactional
    public Page<Question> createQuestion(Pageable pageable,Question question) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", question);

        if (question.getQuestion() == null || question.getQuestion().trim().isEmpty() || !ValidationUtils.isArabic(question.getQuestion())) {
            log.error("Invalid Question:\n{}", question.getQuestion());
            throw new HandledRejection("يرجى التأكد من إدخال السؤال بشكل صحيح وباللغة العربية");
        }

        if (question.getLevel() == null) {
            log.error("Invalid Level:\n{}", (Object) null);
            throw new HandledRejection("يرجى التأكد من إدخال المستوى بشكل صحيح");
        }

        Integer sequenceCount = questionRepository.countByLevelAndDeletedFalse(question.getLevel());

        if(question.getSequence() > sequenceCount + 1){
            log.error("Invalid sequence: {}", question.getSequence());
            throw new HandledRejection("يرجى التأكد من أن رقم التسلسل ضمن النطاق المسموح به");
        }

        if (question.getSequence() < 1){
            log.error("Invalid Sequence:\n{}", question.getSequence());
            throw new HandledRejection("يرجى التأكد من إدخال التسلسل بشكل صحيح");
        }

        log.info("Calling [updateQuestionSequence]");
        updateQuestionSequence(question.getSequence(), true);
        log.info("[updateQuestionSequence] called successfully");

        question.setDeleted(false);

        log.info("Calling [questionRepository].[save]");
        questionRepository.save(question);
        log.info("[questionRepository].[save] called successfully");

        return questionRepository.findAllByDeletedFalse(pageable);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    @Transactional
    public Page<Question> editQuestion(Pageable pageable,Question questionReq) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", questionReq);


        log.info("Calling [questionRepository].[findById]");
        Question question = questionRepository.findByIdAndDeletedFalse(questionReq.getId())
                .orElseThrow(() -> {
                    log.error("Question not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[questionRepository].[findById] called successfully");


        if (questionReq.getQuestion() == null || questionReq.getQuestion().trim().isEmpty() || !ValidationUtils.isArabic(questionReq.getQuestion())) {
            log.error("Invalid Question:\n{}", questionReq.getQuestion());
            throw new HandledRejection("يرجى التأكد من إدخال السؤال بشكل صحيح وباللغة العربية");
        }

        question.setQuestion(questionReq.getQuestion());

        log.info("Calling [questionRepository].[save]");
        questionRepository.save(question);
        log.info("[questionRepository].[save] called successfully");

        return questionRepository.findAllByDeletedFalse(pageable);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    @Transactional
    public Page<Question> deleteQuestion(Pageable pageable, int questionId) throws JsonProcessingException {
        log.info("Question ID: {}", questionId);


        log.info("Calling [questionRepository].[findById]");
        Question question = questionRepository.findByIdAndDeletedFalse(questionId)
                .orElseThrow(() -> {
                    log.error("Question not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[questionRepository].[findById] called successfully");


        updateQuestionSequence(question.getSequence(), false);

        question.setDeleted(true);

        log.info("Calling [questionRepository].[save]");
        questionRepository.save(question);
        log.info("[questionRepository].[save] called successfully");

        return questionRepository.findAllByDeletedFalse(pageable);
    }

    @Transactional
    public void updateQuestionSequence(int sequence, Boolean isIncrement) {
        entityManager.createStoredProcedureQuery("update_question_sequence")
                .registerStoredProcedureParameter("p_seq", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_is_increment", Boolean.class, ParameterMode.IN)
                .setParameter("p_seq", sequence)
                .setParameter("p_is_increment", isIncrement)
                .execute();
    }



}
