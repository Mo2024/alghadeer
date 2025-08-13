package com.mohamed.backend.service;

import com.mohamed.backend.repository.classinfo.assignment.StudentsAssignmentRepository;
import com.mohamed.backend.utils.Logger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentAssignmentService {

    private final StudentsAssignmentRepository studentsAssignmentRepository;

    private final Logger logger;

    @Transactional
    public void createStudentAssignment(Integer assignmentId, Integer classId) {
        log.info("Paratmeters assignmentId: {}, classId {}", assignmentId, classId);

        log.info("Calling [studentsAssignmentRepository].[bulkCreateStudentAssignment]");
        int rowsInserted = studentsAssignmentRepository.bulkCreateStudentAssignment(assignmentId, classId);
        log.info("[studentsAssignmentRepository].[bulkCreateStudentAssignment] called successfully");

        log.info("Number of rows inserted: {}", rowsInserted);
    }
}
