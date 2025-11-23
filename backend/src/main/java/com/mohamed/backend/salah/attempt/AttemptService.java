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
import com.mohamed.backend.users.students.StudentService;
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
    private final StudentService studentService;
    private final EntityManager entityManager;
    private final Logger logger;

    @Transactional
    public List<StudentSalahQuestionView> createSalahAttempt(List<Integer> selectedSubjects, int studentId) throws JsonProcessingException {
        logger.logJsonObject("Request parameter 1:\n{}", selectedSubjects);
        logger.logJsonObject("Request parameter 2 studentId: {}", studentId);

        StudentLevel studentLevel = studentLevelService.getStudentLevelObjectByStudentId(studentId);

        if(studentLevel == null){
            Student studentRef = entityManager.getReference(Student.class, studentId);

            studentLevel = StudentLevel.builder()
                    .student(studentRef)
                    .level(Level.ONE)
                    .build();
            studentLevel = studentLevelService.createStudentLevel(studentLevel);
        }

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

        return listOfQuestions;
    }

    public List<SalahAttemptView> getLatestAttempts(Integer studentId) throws JsonProcessingException {
        logger.logJsonObject("Request parameter studentId: {}", studentId);

        log.info("Calling [studentLevelService].[getStudentLevelObjectById]");
        StudentLevel studentLevel = studentLevelService.getStudentLevelObjectByStudentId(studentId);
        log.info("[studentLevelService].[getStudentLevelObjectById] called successfully");

        logger.logJsonObject("Student level:\n{}", studentLevel);

        if(studentLevel == null){
            log.error("No previous attempts for the student");
            throw new HandledRejection("لا توجد محاولات سابقة للطالب");
        }

        log.info("Calling [subjectRepository].[subjectsIdByLevel]");
        List<Integer> subjectsId = subjectRepository.subjectsIdByLevel(studentLevel.getLevel());
        log.info("[subjectRepository].[subjectsIdByLevel] called successfully");

        log.info("Calling [studentSalahAttemptRepository].[getLatestStudentAttempts]");
        List<SalahAttemptView> latestStudentAttempts = studentSalahAttemptRepository.getLatestStudentAttempts(studentLevel.getStudent().getId(), subjectsId);
        log.info("[studentSalahAttemptRepository].[getLatestStudentAttempts] called successfully");

        return latestStudentAttempts;
    }

}
