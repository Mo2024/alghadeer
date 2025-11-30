package com.mohamed.backend.salah.attempt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestionRepository;
import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestionView;
import com.mohamed.backend.salah.level.StudentLevel;
import com.mohamed.backend.salah.level.StudentLevelService;
import com.mohamed.backend.salah.questions.QuestionRepository;
import com.mohamed.backend.salah.questions.subjects.Subject;
import com.mohamed.backend.salah.questions.subjects.SubjectRepository;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.utils.methods.Logger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttemptService {

    private final StudentAttemptRepository studentSalahAttemptRepository;
    private final StudentSalahQuestionRepository studentSalahQuestionRepository;
    private final StudentLevelService studentLevelService;
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;
    private final Logger logger;

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR','INSTRUCTOR')")
    public AttemptAndQuestionsDto createSalahAttempt(List<Integer> selectedSubjects, int studentId) throws JsonProcessingException {
        logger.logJsonObject("Request parameter 1:\n{}", selectedSubjects);
        logger.logJsonObject("Request parameter 2 studentId: {}", studentId);

        if (selectedSubjects.isEmpty()){
            log.error("No subjects selected");
            throw new HandledRejection("الرجاء اختيار موضوع واحد على الأقل");
        }

        log.info("Calling [studentLevelService].[getStudentLevelObjectByStudentId]");
        StudentLevel studentLevel = studentLevelService.getStudentLevelObjectByStudentId(studentId);
        log.info("[studentLevelService].[getStudentLevelObjectByStudentId] called successfully");

        log.info("Calling [subjectRepository].[countOfSelectedSubjectsInLevel]");
        int count = subjectRepository.countOfSelectedSubjectsInLevel(selectedSubjects, studentLevel.getLevel().toString());
        log.info("[subjectRepository].[countOfSelectedSubjectsInLevel] called successfully");

        log.info("{}",count);
        log.info("{}",selectedSubjects.size());

        //This is a validation to check if the selected subjects actually belong to that level or not
        if (selectedSubjects.size() != count) {
            log.error("At least one of the subjects does not exist in that level");
            throw new HandledRejection("واحد أو أكثر من المواضيع المحددة ليست من ضمن المستوى المحدد");
        }

        for (Integer subjectId: selectedSubjects){
            log.info("Calling [questionRepository].[existsBySubject_Id]");
            Boolean existBySubjectId = questionRepository.existsBySubject_Id(subjectId);
            log.info("[questionRepository].[existsBySubject_Id] called successfully");

            log.info("{}", existBySubjectId);

            if(!existBySubjectId){
                log.info("Calling [subjectRepository].[findById]");
                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> {
                            log.error("Subject does not exist:\n{}", subjectId);
                            return new HandledRejection("البيانات غير صحيحة");
                        });
                log.info("[subjectRepository].[findById] called successfully");

                logger.logJsonObjectError("No questions exist for the specified subject\n {}", subject);
                throw new HandledRejection( "لا توجد أسئلة للموضوع: "+ subject.getName());

            }
        }

        StudentAttempt attempt = StudentAttempt.builder()
                .studentLevel(studentLevel)
                .attemptDateTime(LocalDateTime.now())
                .subjects(selectedSubjects)
                .isCompleted(false)
                .build();

        log.info("Calling [studentSalahAttemptRepository].[save]");
        attempt = studentSalahAttemptRepository.save(attempt);
        log.info("[studentSalahAttemptRepository].[save] called successfully");

        log.info("Calling [questionRepository].[getListOfQuestions]");
        List<StudentSalahQuestionView> listOfQuestions = studentSalahQuestionRepository.getFreshStudentSalahQuestions(selectedSubjects, studentLevel.getLevel(), attempt.getId());
        log.info("[questionRepository].[getListOfQuestions] called successfully");

        return AttemptAndQuestionsDto.builder()
                .salahQuestionsRes(listOfQuestions)
                .studentAttempt(attempt)
                .build();
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR','INSTRUCTOR')")
    public LatestAttemptsWithStudentLevelDto getLatestAttemptsAndStudentLevel(Integer studentId) throws JsonProcessingException {
        logger.logJsonObject("Request parameter studentId: {}", studentId);

        log.info("Calling [studentLevelService].[getStudentLevelObjectByStudentId]");
        StudentLevel studentLevel = studentLevelService.getStudentLevelObjectByStudentId(studentId);
        log.info("[studentLevelService].[getStudentLevelObjectByStudentId] called successfully");

        logger.logJsonObject("Student level:\n{}", studentLevel);

        log.info("Calling [subjectRepository].[subjectsIdByLevel]");
        List<Integer> subjectsId = subjectRepository.subjectsIdByLevel(studentLevel.getLevel());
        log.info("[subjectRepository].[subjectsIdByLevel] called successfully");

        log.info("Calling [studentSalahAttemptRepository].[getLatestStudentAttempts]");
        List<SalahAttemptView> latestStudentAttempts = studentSalahAttemptRepository.getLatestStudentAttempts(studentLevel.getStudent().getId(), subjectsId);
        log.info("[studentSalahAttemptRepository].[getLatestStudentAttempts] called successfully");

        return LatestAttemptsWithStudentLevelDto
                .builder()
                .latestAttempts(latestStudentAttempts)
                .studentLevel(studentLevel)
                .build();
    }

}
