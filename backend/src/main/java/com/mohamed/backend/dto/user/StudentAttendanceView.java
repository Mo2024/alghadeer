package com.mohamed.backend.dto.user;

public interface StudentAttendanceView {
    StudentView getStudent();

    boolean getIsPresent();
    Integer getId();
    SessionIdOnly getSession();

    interface SessionIdOnly {
        Integer getId();
    }}
