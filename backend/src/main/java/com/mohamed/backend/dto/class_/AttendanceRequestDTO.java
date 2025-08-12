package com.mohamed.backend.dto.class_;

import com.mohamed.backend.model.classinfo.Attendance;
import com.mohamed.backend.model.classinfo.Session;
import lombok.Data;

import java.util.List;

@Data
public class AttendanceRequestDTO {

    private Session session;

    private List<Attendance> attendances;
}
