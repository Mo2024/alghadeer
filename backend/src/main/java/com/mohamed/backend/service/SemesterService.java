package com.mohamed.backend.service;

import com.mohamed.backend.dto.Response;
import com.mohamed.backend.exceptions.UnhandledRejection;
import com.mohamed.backend.model.Semester;
import com.mohamed.backend.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SemesterService {

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Response createSemester(Semester semesterReq){
        log.info("Semester Request info: {}", semesterReq);

        if (semesterReq.getName() == null || semesterReq.getName().trim().isEmpty() || !ValidationUtils.isArabic(semesterReq.getName())) {
            log.error("Invalid name: {}", semesterReq.getName());
            throw new UnhandledRejection("يرجى التأكد من إدخال الاسم بشكل صحيح وباللغة العربية");
        }

        if (semesterReq.getSemester() == null || semesterReq.getSemester().toString().trim().isEmpty() || !ValidationUtils.isValidSemester(semesterReq.getSemester().toString())) {
            log.error("Invalid Invalid Semester: {}", semesterReq.getSemester());
            throw new UnhandledRejection("يرجى التأكد من إدخال الفصل صحيح");
        }

        if (semesterReq.getStartDate() == null || !ValidationUtils.isPastDate(semesterReq.getStartDate())) {
            log.error("Invalid start date: {}", semesterReq.getStartDate());
            throw new UnhandledRejection("يرجى التأكد من إدخال تاريخ إبتداء الفصل بشكل صحيح");
        }

        if (semesterReq.getEndDate() == null || !ValidationUtils.isPastDate(semesterReq.getEndDate())) {
            log.error("Invalid end date: {}", semesterReq.getEndDate());
            throw new UnhandledRejection("يرجى التأكد من إدخال تاريخ إبتداء الفصل بشكل صحيح");
        }


    }
