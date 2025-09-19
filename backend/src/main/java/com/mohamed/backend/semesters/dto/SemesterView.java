package com.mohamed.backend.semesters.dto;

import com.mohamed.backend.semesters.SemesterList;

import java.time.LocalDate;

public interface SemesterView {
    Integer getId();

    String getName();

    SemesterList getSemester();

    LocalDate getStartDate();

    LocalDate getEndDate();

    Boolean getActive();
}
