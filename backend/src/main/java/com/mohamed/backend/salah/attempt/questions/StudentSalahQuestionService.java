package com.mohamed.backend.salah.attempt.questions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.salah.attempt.StudentAttempt;
import com.mohamed.backend.salah.attempt.StudentAttemptRepository;
import com.mohamed.backend.utils.Response;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.utils.methods.Logger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentSalahQuestionService {

    private final Logger logger;
    private final StudentSalahQuestionRepository studentSalahQuestionRepository;
    private final StudentAttemptRepository studentAttemptRepository;

    public List<StudentSalahQuestionView> getQuestionsOfAttempt(Integer attemptId) throws JsonProcessingException {
        logger.logJsonObject("Request parameter attemptId:\n{}", attemptId);

        StudentAttempt studentAttempt = studentAttemptRepository.findById(attemptId)
                .orElseThrow(() -> {
            log.error("Attempt does not exist:\n{}", attemptId);
            return new HandledRejection("الإختبار غير موجود");
        });

        log.info("Calling [questionRepository].[findByStudentSalahAttemptId]");
        List<StudentSalahQuestionView> listOfQuestions = studentSalahQuestionRepository.findByStudentSalahAttemptId(attemptId);
        log.info("[questionRepository].[findByStudentSalahAttemptId] called successfully");

        if(listOfQuestions.isEmpty()){
            log.info("Calling [questionRepository].[getFreshStudentSalahQuestions]");
            listOfQuestions = studentSalahQuestionRepository.getFreshStudentSalahQuestions(studentAttempt.getSubjects(), studentAttempt.getStudentLevel().getLevel(), studentAttempt.getId());
            log.info("[questionRepository].[getFreshStudentSalahQuestions] called successfully");
        }


        return listOfQuestions;
    }

    @Transactional
    public Response saveAttempt(List<StudentSalahQuestion> listOfQuestions, int attemptId, boolean isSubmitAttempt) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", listOfQuestions);
        logger.logJsonObject("Request attemptId: {}", attemptId);

        log.info("listOfQuestions count: {}", listOfQuestions.size());

        StudentAttempt studentAttempt = studentAttemptRepository.findById(attemptId)
                .orElseThrow(() -> {
                    log.error("Attempt does not exist:\n{}", attemptId);
                    return new HandledRejection("الإختبار غير موجود");
                });

        List<Integer> subjectsId = studentAttempt.getSubjects();

        int questionsCount = studentSalahQuestionRepository.countByStudentSalahAttemptId(attemptId);
        log.info("questionsCount: {}", questionsCount);

        log.info("Calling [questionRepository].[getFreshStudentSalahQuestionsCount]");
        int freshListOfQuestionsCount = studentSalahQuestionRepository.getFreshStudentSalahQuestionsCount(
                subjectsId,
                studentAttempt.getStudentLevel().getLevel(),
                studentAttempt.getId());
        log.info("[questionRepository].[getFreshStudentSalahQuestionsCount] called successfully");

        log.info("freshListOfQuestionsCount: {}", freshListOfQuestionsCount);

        if ((questionsCount != listOfQuestions.size() && questionsCount != 0) || freshListOfQuestionsCount != listOfQuestions.size()) {
            log.error("Invalid data provided");
            throw new HandledRejection("البيانات غير صالحة");
        }


        for (StudentSalahQuestion salahQuestion : listOfQuestions) {
            // To check if the id of that salah question actually exists
            boolean existsById = salahQuestion.getId() != null && studentSalahQuestionRepository.existsById(salahQuestion.getId());


            // To check if the attempt provided matches the one in the array question object
            boolean attemptIdMatch = salahQuestion.getStudentSalahAttempt().getId().equals(attemptId);

            // To check if the subject id of the question is included in the subjects array in student attempt object
            boolean subjectIdExistsInAttempt = studentAttempt.getSubjects().contains(salahQuestion.getQuestion().getSubjectId());

            if (!attemptIdMatch || !subjectIdExistsInAttempt || (questionsCount != 0 && !existsById)) {
                logger.logJsonObject("Invalid data provided:\n{}", salahQuestion);
                throw new HandledRejection("البيانات غير صالحة");
            }

            if (isSubmitAttempt) {

                if (salahQuestion.getEvaluation() == null) {
                    logger.logJsonObject("Invalid data provided:\n{}", salahQuestion);
                    throw new HandledRejection("التقوييم غير صالح");
                }

                if (salahQuestion.getEvaluation().equals(Evaluation.YANSAA_AW_LA_YAALAM) || salahQuestion.getEvaluation().equals(Evaluation.GHAYR_MOTAMAKEN)) {
                    salahQuestion.setGrade(0);
                } else if (salahQuestion.getEvaluation().equals(Evaluation.LA_BAS)) {
                    salahQuestion.setGrade(1);
                } else if (salahQuestion.getEvaluation().equals(Evaluation.ITQAN)) {
                    salahQuestion.setGrade(2);
                } else {
                    logger.logJsonObject("Something went wrong:\n{}", salahQuestion);
                    throw new HandledRejection("يرجي التأكد من البيانات");
                }
            }

        }

        if (isSubmitAttempt) {
            studentAttempt.setIsCompleted(true);
            studentAttemptRepository.save(studentAttempt);
        }

        //need validation for manual grades from postman etc
        studentSalahQuestionRepository.saveAll(listOfQuestions);

        return new Response("Attempt saved successfully");
    }

    @Transactional
    public Response submitAttempt(List<StudentSalahQuestion> questionList, int attemptId) throws JsonProcessingException {
        logger.logJsonObject("Request parameter 1 questionList :\n{}", questionList);
        logger.logJsonObject("Request parameter 1 attemptId: {}", attemptId);

        saveAttempt(
                questionList,
                attemptId,
                true
        );

        return new Response("Attempt Completed successfully");
    }

}
