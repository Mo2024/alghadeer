package com.mohamed.backend.dto.class_;

import com.mohamed.backend.dto.user.StudentView;

public interface AttendanceView {
    StudentView getStudent();

    boolean getIsPresent();
}
