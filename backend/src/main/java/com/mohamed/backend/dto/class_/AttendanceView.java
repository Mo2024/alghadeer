package com.mohamed.backend.dto.class_;

import com.mohamed.backend.dto.user.StudentView;

public interface AttendanceView {
    StudentView getStudent();

    boolean getIsPresent();
    Integer getId();
    SessionIdOnly getSession();

    interface SessionIdOnly {
        Integer getId();
    }}
