package com.mohamed.backend.users.students.dto;

public interface StudentAttendanceView {
    StudentView getStudent();

    boolean getIsPresent();
    Integer getId();
    SessionIdOnly getSession();

    interface SessionIdOnly {
        Integer getId();
    }}
