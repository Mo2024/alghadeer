package com.mohamed.backend.salah.attempt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestionRepository;
import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestionView;
import com.mohamed.backend.salah.level.StudentLevel;
import com.mohamed.backend.salah.level.StudentLevelService;
import com.mohamed.backend.salah.questions.Level;
import com.mohamed.backend.salah.questions.Question;
import com.mohamed.backend.salah.questions.QuestionRepository;
import com.mohamed.backend.salah.questions.subjects.Subject;
import com.mohamed.backend.salah.questions.subjects.SubjectRepository;
import com.mohamed.backend.users.students.Student;
import com.mohamed.backend.users.students.StudentRepository;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.utils.methods.Logger;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;
    private final EntityManager entityManager;
    private final Logger logger;

    @Transactional
    public List<StudentSalahQuestionView> createSalahAttempt(List<Integer> selectedSubjects, int studentId) throws JsonProcessingException {
        logger.logJsonObject("Request parameter 1:\n{}", selectedSubjects);
        logger.logJsonObject("Request parameter 2 studentId: {}", studentId);

        Level level = studentLevelService.getStudentLevel(studentId);

        if(level == null){
            Student studentRef = entityManager.getReference(Student.class, studentId);

            StudentLevel studentLevel = StudentLevel.builder()
                    .student(studentRef)
                    .level(Level.ONE)
                    .build();
            studentLevelService.createStudentLevel(studentLevel);
            level = Level.ONE;
        }

        log.info("Calling [subjectRepository].[countOfSelectedSubjectsInLevel]");
        int count = subjectRepository.countOfSelectedSubjectsInLevel(selectedSubjects, level.toString());
        log.info("[subjectRepository].[countOfSelectedSubjectsInLevel] called successfully");

        log.info("{}",count);
        log.info("{}",selectedSubjects.size());

        if (selectedSubjects.size() != count) {
            log.error("At least one of the subjects does not exist in that level");
            throw new HandledRejection("واحد أو أكثر من المواضيع المحددة ليست من ضمن المستوى المحدد");
        }

        log.info("Calling [studentRepository].[findById]");
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.error("Student does not exist:\n{}", studentId);
                    return new HandledRejection("الطالب غير موجود");
                });
        log.info("[studentRepository].[findById] called successfully");


        StudentAttempt attempt = StudentAttempt.builder()
                .student(student)
                .attemptDateTime(LocalDateTime.now())
                .build();

        log.info("Calling [studentSalahAttemptRepository].[save]");
        attempt = studentSalahAttemptRepository.save(attempt);
        log.info("[studentSalahAttemptRepository].[save] called successfully");

        log.info("Calling [questionRepository].[getListOfQuestions]");
        List<StudentSalahQuestionView> listOfQuestions = studentSalahQuestionRepository.getFreshStudentSalahQuestions(selectedSubjects, level, attempt.getId());
        log.info("[questionRepository].[getListOfQuestions] called successfully");

        return listOfQuestions;
    }



}
