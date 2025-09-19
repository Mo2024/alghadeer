package com.mohamed.backend.classes.dto;

import com.mohamed.backend.users.students.dto.StudentView;

public interface AttendanceView {
    StudentView getStudent();

    boolean getIsPresent();
    Integer getId();
    SessionIdOnly getSession();

    interface SessionIdOnly {
        Integer getId();
    }}
