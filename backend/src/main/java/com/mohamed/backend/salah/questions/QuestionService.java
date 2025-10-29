package com.mohamed.backend.salah.questions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.salah.questions.subjects.Subject;
import com.mohamed.backend.salah.questions.subjects.SubjectArea;
import com.mohamed.backend.salah.questions.subjects.SubjectAreaRepository;
import com.mohamed.backend.salah.questions.subjects.SubjectRepository;
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

import java.awt.geom.Area;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    @PersistenceContext
    private EntityManager entityManager;
    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectAreaRepository subjectAreaRepository;
    private final Logger logger;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR','INSTRUCTOR')")
    public List<Question> getQuestionsByLevel(Level level) {
        log.info("Level param: {}", level);
        return questionRepository.findAllByLevelAndDeletedFalseOrderBySequenceAsc(level);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    @Transactional
    public List<Question> createQuestion(Level level, Question question) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", question);

        if (question.getQuestion() == null || question.getQuestion().trim().isEmpty() || !ValidationUtils.isArabic(question.getQuestion())) {
            log.error("Invalid Question:\n{}", question.getQuestion());
            throw new HandledRejection("يرجى التأكد من إدخال السؤال بشكل صحيح وباللغة العربية");
        }

        if (question.getLevel() == null) {
            log.error("Invalid Level:\n{}", (Object) null);
            throw new HandledRejection("يرجى التأكد من إدخال المستوى بشكل صحيح");
        }

        if (question.getIsPillar() == null) {
            log.error("Invalid isPillar value: null");
            throw new HandledRejection("يرجى تحديد ما إذا كان السؤال من الأركان أو لا");
        }

        log.info("Calling [subjectRepository].[findById]");
        subjectRepository.findById(question.getSubject().getId())
                .orElseThrow(() -> {
                    log.error("Subject not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[subjectRepository].[findById] called successfully");

        log.info("Calling [subjectAreaRepository].[findById]");
        SubjectArea area = subjectAreaRepository.findById(question.getArea().getId())
                .orElseThrow(() -> {
                    log.error("Subject area not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[subjectAreaRepository].[findById] called successfully");

        if(!area.getSubject().getId().equals(question.getSubject().getId())){
            log.error("area is not related to subject");
            throw  new HandledRejection("يرجى التأكد من البيانات");
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
        updateQuestionSequence(question.getSequence(), true, question.getLevel());
        log.info("[updateQuestionSequence] called successfully");

        question.setDeleted(false);

        log.info("Calling [questionRepository].[save]");
        questionRepository.save(question);
        log.info("[questionRepository].[save] called successfully");

        return questionRepository.findAllByLevelAndDeletedFalseOrderBySequenceAsc(level);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    @Transactional
    public List<Question> editQuestion(Level level, Question questionReq) throws JsonProcessingException {
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

        return questionRepository.findAllByLevelAndDeletedFalseOrderBySequenceAsc(level);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN')")
    @Transactional
    public List<Question> deleteQuestion(Level level, int questionId) throws JsonProcessingException {
        log.info("Question ID: {}", questionId);


        log.info("Calling [questionRepository].[findById]");
        Question question = questionRepository.findByIdAndDeletedFalse(questionId)
                .orElseThrow(() -> {
                    log.error("Question not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[questionRepository].[findById] called successfully");


        updateQuestionSequence(question.getSequence(), false, question.getLevel());

        question.setDeleted(true);

        log.info("Calling [questionRepository].[save]");
        questionRepository.save(question);
        log.info("[questionRepository].[save] called successfully");

        return questionRepository.findAllByLevelAndDeletedFalseOrderBySequenceAsc(level);
    }

    @Transactional
    public void updateQuestionSequence(int sequence, Boolean isIncrement, Level level) {
        entityManager.createStoredProcedureQuery("update_question_sequence")
                .registerStoredProcedureParameter("p_seq", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_is_increment", Boolean.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_level", String.class, ParameterMode.IN)
                .setParameter("p_seq", sequence)
                .setParameter("p_is_increment", isIncrement)
                .setParameter("p_level", level.toString())
                .execute();
    }



}
