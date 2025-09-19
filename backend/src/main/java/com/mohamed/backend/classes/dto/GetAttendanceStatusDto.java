package com.mohamed.backend.classes.dto;

import com.mohamed.backend.users.students.dto.StudentAttendanceView;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GetAttendanceStatusDto {
    List<StudentAttendanceView> students;
    boolean attendanceTaken;
    List<AttendanceView> attendanceList;
}
