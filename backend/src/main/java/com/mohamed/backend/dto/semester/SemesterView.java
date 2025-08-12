package com.mohamed.backend.dto.semester;

import com.mohamed.backend.model.enums.SemesterList;

import java.time.LocalDate;

public interface SemesterView {
    Integer getId();

    String getName();

    SemesterList getSemester();

    LocalDate getStartDate();

    LocalDate getEndDate();

    Boolean getActive();
}
