package com.mohamed.backend.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.dto.class_.ClassView;

import java.time.LocalDate;

public interface SessionView {
    Integer getId();

    LocalDate getDate();

    @JsonProperty("class")
    ClassView getSemesterClass();
}
