package com.mohamed.backend.salah.attempt.questions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.salah.attempt.dto.AttemptAndQuestionsDto;
import com.mohamed.backend.salah.attempt.StudentAttempt;
import com.mohamed.backend.salah.attempt.StudentAttemptRepository;
import com.mohamed.backend.salah.attempt.dto.SubjectJsonDto;
import com.mohamed.backend.utils.Response;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.utils.methods.Logger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentSalahQuestionService {

    private final Logger logger;
    private final StudentSalahQuestionRepository studentSalahQuestionRepository;
    private final StudentAttemptRepository studentAttemptRepository;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR','INSTRUCTOR')")
    public AttemptAndQuestionsDto getQuestionsOfAttempt(Integer attemptId) throws JsonProcessingException {
        logger.logJsonObject("Request parameter attemptId:\n{}", attemptId);

        log.info("Calling [studentAttemptRepository].[findById]");
        StudentAttempt studentAttempt = studentAttemptRepository.findById(attemptId)
                .orElseThrow(() -> {
            log.error("Attempt does not exist:\n{}", attemptId);
            return new HandledRejection("الإختبار غير موجود");
        });
        log.info("[studentAttemptRepository].[findById] called successfully");

        log.info("Calling [questionRepository].[findByStudentSalahAttemptId]");
        List<StudentSalahQuestionView> listOfQuestions = studentSalahQuestionRepository.findByStudentSalahAttemptIdOrderByIdAsc(attemptId);
        log.info("[questionRepository].[findByStudentSalahAttemptId] called successfully");

        if(listOfQuestions.isEmpty()){
            log.info("Calling [questionRepository].[getFreshStudentSalahQuestions]");
            listOfQuestions = studentSalahQuestionRepository.getFreshStudentSalahQuestions(getSubjectsId(studentAttempt.getSubjects()), studentAttempt.getStudentLevel().getLevel(), studentAttempt.getId());
            log.info("[questionRepository].[getFreshStudentSalahQuestions] called successfully");
        }


        return AttemptAndQuestionsDto.builder()
                .salahQuestionsRes(listOfQuestions)
                .studentAttempt(studentAttempt)
                .build();
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR','INSTRUCTOR')")
    public Response saveAttempt(AttemptAndQuestionsDto attemptAndQuestionsDto, boolean isSubmitAttempt) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", attemptAndQuestionsDto);

        log.info("listOfQuestions count: {}", attemptAndQuestionsDto.getSalahQuestionsReq().size());

        log.info("Calling [studentAttemptRepository].[findById]");
        StudentAttempt studentAttempt = studentAttemptRepository.findById(attemptAndQuestionsDto.getStudentAttempt().getId())
                .orElseThrow(() -> {
                    log.error("Attempt does not exist:\n{}", attemptAndQuestionsDto.getStudentAttempt().getId());
                    return new HandledRejection("الإختبار غير موجود");
                });
        log.info("[studentAttemptRepository].[findById] called successfully");

        List<Integer> subjectsId = getSubjectsId(studentAttempt.getSubjects());

        log.info("Calling [studentSalahQuestionRepository].[countByStudentSalahAttemptId]");
        int questionsCount = studentSalahQuestionRepository.countByStudentSalahAttemptId(attemptAndQuestionsDto.getStudentAttempt().getId());
        log.info("[studentSalahQuestionRepository].[countByStudentSalahAttemptId] called successfully");

        log.info("questionsCount: {}", questionsCount);

        log.info("Calling [questionRepository].[getFreshStudentSalahQuestionsCount]");
        int freshListOfQuestionsCount = studentSalahQuestionRepository.getFreshStudentSalahQuestionsCount(
                subjectsId,
                studentAttempt.getStudentLevel().getLevel(),
                studentAttempt.getId());
        log.info("[questionRepository].[getFreshStudentSalahQuestionsCount] called successfully");

        log.info("freshListOfQuestionsCount: {}", freshListOfQuestionsCount);

        if ((questionsCount != attemptAndQuestionsDto.getSalahQuestionsReq().size() && questionsCount != 0) || freshListOfQuestionsCount != attemptAndQuestionsDto.getSalahQuestionsReq().size()) {
            log.error("Invalid data provided");
            throw new HandledRejection("البيانات غير صالحة");
        }


        for (StudentSalahQuestion salahQuestion : attemptAndQuestionsDto.getSalahQuestionsReq()) {
            // To check if the id of that salah question actually exists
            log.info("Calling [studentSalahQuestionRepository].[existsById]");
            boolean existsById = salahQuestion.getId() != null && studentSalahQuestionRepository.existsById(salahQuestion.getId());
            log.info("[studentSalahQuestionRepository].[existsById] called successfully");


            // To check if the attempt provided matches the one in the array question object
            boolean attemptIdMatch = salahQuestion.getStudentSalahAttempt().getId().equals(attemptAndQuestionsDto.getStudentAttempt().getId());

            // To check if the subject id of the question is included in the subjects array in student attempt object
            boolean subjectIdExistsInAttempt = getSubjectsId(studentAttempt.getSubjects()).contains(salahQuestion.getQuestion().getSubjectId());

            if (!attemptIdMatch || !subjectIdExistsInAttempt || (questionsCount != 0 && !existsById)) {
                logger.logJsonObject("Invalid data provided:\n{}", salahQuestion);
                throw new HandledRejection("البيانات غير صالحة");
            }

            if (isSubmitAttempt) {

                if (salahQuestion.getEvaluation() == null) {
                    logger.logJsonObject("Invalid data provided:\n{}", salahQuestion);
                    throw new HandledRejection("يرجى اختيار التقييم");
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
        }

        studentAttempt.setComments(attemptAndQuestionsDto.getStudentAttempt().getComments());

        if (studentAttempt.getSubjects().size() != attemptAndQuestionsDto.getStudentAttempt().getSubjects().size()){
            logger.logJsonObject("Subjects size in request does not match the one stored :\n{}", attemptAndQuestionsDto.getStudentAttempt().getSubjects());
            throw new HandledRejection("يرجي التأكد من البيانات");
        }

        Set<Integer> subjectsIdReqSet = new HashSet<>();

        for (SubjectJsonDto subject : attemptAndQuestionsDto.getStudentAttempt().getSubjects()) {
            subjectsIdReqSet.add(subject.getSubjectId());

            if (isSubmitAttempt && subject.getPassed() == null) {
                logger.logJsonObject(
                        "Subject has null passed' value. Subject object: \n{}",
                        subject
                );
                throw new HandledRejection("يرجى تحديد ما إذا كان الطالب ناجحاً أم راسباً");
            }

        }

        for (SubjectJsonDto subject : studentAttempt.getSubjects()) {
            if (!subjectsIdReqSet.contains(subject.getSubjectId())) {
                logger.logJsonObject(
                        "Subject was not found in Subjects Req :\n{}",
                        attemptAndQuestionsDto.getStudentAttempt().getSubjects()
                );
                throw new HandledRejection("يرجي التأكد من البيانات");
            }
        }

        studentAttempt.setSubjects(attemptAndQuestionsDto.getStudentAttempt().getSubjects());

        log.info("Calling [studentAttemptRepository].[save]");
        studentAttemptRepository.save(studentAttempt);
        log.info("[studentAttemptRepository].[save] called successfully");

        //need validation for manual grades from postman etc
        log.info("Calling [studentSalahQuestionRepository].[saveAll]");
        studentSalahQuestionRepository.saveAll(attemptAndQuestionsDto.getSalahQuestionsReq());
        log.info("[studentSalahQuestionRepository].[saveAll] called successfully");

        return new Response("Attempt saved successfully");
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR','INSTRUCTOR')")
    public Response submitAttempt(AttemptAndQuestionsDto attemptAndQuestionsDto) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", attemptAndQuestionsDto);

        log.info("Calling [saveAttempt]");
        saveAttempt(
                attemptAndQuestionsDto,
                true
        );
        log.info("[saveAttempt] called successfully");

        return new Response("Attempt Completed successfully");
    }

    private List<Integer> getSubjectsId(List<SubjectJsonDto> subjectsJsonDto) {
        List<Integer> subjectsId = new ArrayList<>();

        for (SubjectJsonDto subjectJsonDto : subjectsJsonDto) {
            subjectsId.add(subjectJsonDto.getSubjectId());
        }

        return subjectsId;
    }

}
