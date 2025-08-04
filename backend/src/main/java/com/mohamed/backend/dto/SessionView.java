package com.mohamed.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.model.topics.SubTopic;
import java.time.LocalDate;

public interface SessionView {
    Integer getId();
    LocalDate getDate();
    SubTopic getSubTopic();
    boolean getCancelled();
    @JsonProperty("class")
    ClassView getSemesterClass();
}
