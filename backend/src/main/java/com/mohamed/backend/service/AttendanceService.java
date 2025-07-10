package com.mohamed.backend.service;

import com.mohamed.backend.dto.AttendanceRequestDTO;
import com.mohamed.backend.dto.Response;
import com.mohamed.backend.repository.AttendanceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'INSTRUCTOR')")
    public Response takeAttendance(AttendanceRequestDTO attendanceRequest){
        log.info("executing method [takeAttendance]");



        log.info("[takeAttendance] executed successfully");
        return new Response("تم تسجيل الحضور بنجاح");
    }
}
