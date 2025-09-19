package com.mohamed.backend.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.classes.dto.ClassView;

import java.time.LocalDate;

public interface SessionView {
    Integer getId();

    LocalDate getDate();

    @JsonProperty("class")
    ClassView getSemesterClass();
}
