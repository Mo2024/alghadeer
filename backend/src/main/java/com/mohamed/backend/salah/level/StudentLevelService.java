package com.mohamed.backend.salah.level;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamed.backend.salah.questions.Level;
import com.mohamed.backend.utils.Response;
import com.mohamed.backend.utils.exceptions.HandledRejection;
import com.mohamed.backend.utils.methods.Logger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentLevelService {

    private final StudentLevelRepository studentLevelRepository;
    private final Logger logger;

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR','INSTRUCTOR')")
    public StudentLevel createStudentLevel(StudentLevel studentLevelParam) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", studentLevelParam);

        log.info("Calling [studentLevelRepository].[findByStudentId]");
        studentLevelRepository.findByStudentId(studentLevelParam.getStudent().getId())
                .ifPresent(sl -> {
                    log.error("Student level already exists");
                    throw new HandledRejection("مستوى الطالب مسجَّل مسبقًا");
                });
        log.info("[studentLevelRepository].[findByStudentId] called successfully");

        log.info("Calling [studentLevelRepository].[save]");
        StudentLevel studentLevel = studentLevelRepository.save(studentLevelParam);
        log.info("[studentLevelRepository].[save] called successfully");

        return studentLevel;
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR','INSTRUCTOR')")
    public Response updateStudentLevel(StudentLevel studentLevelParam) throws JsonProcessingException {
        logger.logJsonObject("Request parameter:\n{}", studentLevelParam);

        log.info("Calling [studentLevelRepository].[findByStudentId]");
        StudentLevel studentLevel = studentLevelRepository.findByStudentId(studentLevelParam.getStudent().getId())
                .orElseThrow(() -> {
                    log.error("Student level not found");
                    return new HandledRejection("يرجى التأكد من البيانات");
                });
        log.info("[studentLevelRepository].[findByStudentId] called successfully");

        studentLevel.setLevel(studentLevelParam.getLevel());

        log.info("Calling [studentLevelRepository].[save]");
        studentLevelRepository.save(studentLevel);
        log.info("[studentLevelRepository].[save] called successfully");


        return new Response("تم تحديث مستوى الطالب بنجاح");
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR','INSTRUCTOR')")
    public StudentLevel getStudentLevelObjectByStudentId(int studentId){
        log.info("Request parameter studentId: {}", studentId);

        log.info("Calling [studentLevelRepository].[findByStudentId]");
        StudentLevel studentLevel = studentLevelRepository.findByStudentId(studentId)
                .orElse(null);
        log.info("[studentLevelRepository].[findByStudentId] called successfully");

        return studentLevel;
    }
}
