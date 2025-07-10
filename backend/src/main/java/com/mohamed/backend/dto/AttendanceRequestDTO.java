package com.mohamed.backend.dto;

import com.mohamed.backend.model.classinfo.Session;
import com.mohamed.backend.model.user.Student;
import lombok.Data;

import java.util.List;

@Data
public class AttendanceRequestDTO {

    private Session session;

    private List<Student> students;
}
