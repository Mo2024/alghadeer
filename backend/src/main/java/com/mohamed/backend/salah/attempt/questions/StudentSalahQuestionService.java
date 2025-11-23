package com.mohamed.backend.salah.attempt.questions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.salah.attempt.StudentAttempt;
import com.mohamed.backend.salah.attempt.StudentAttemptRepository;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.utils.methods.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

        List<StudentSalahQuestionView> listOfQuestions = studentSalahQuestionRepository.findByStudentSalahAttemptId(attemptId);

        if(listOfQuestions.isEmpty()){
            log.info("Calling [questionRepository].[getListOfQuestions]");
            listOfQuestions = studentSalahQuestionRepository.getFreshStudentSalahQuestions(studentAttempt.getSubjects(), studentAttempt.getStudentLevel().getLevel(), studentAttempt.getId());
            log.info("[questionRepository].[getListOfQuestions] called successfully");
        }

        return listOfQuestions;
    }
}
