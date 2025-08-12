package com.mohamed.backend.dto.class_;

import com.mohamed.backend.dto.user.StudentAttendanceView;
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
