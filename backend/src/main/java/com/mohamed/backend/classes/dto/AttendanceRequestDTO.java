package com.mohamed.backend.classes.dto;

import com.mohamed.backend.sessions.Attendance;
import com.mohamed.backend.sessions.Session;
import lombok.Data;

import java.util.List;

@Data
public class AttendanceRequestDTO {

    private Session session;

    private List<Attendance> attendances;
}
