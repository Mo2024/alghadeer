package com.mohamed.backend.dto;

import com.mohamed.backend.model.classinfo.Attendance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GetAttendanceStatusDto {
    List<StudentAttendanceView> students;
    boolean attendanceTaken;
    List<AttendanceView> attendanceList;
}
