package com.mohamed.backend.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.classes.Class;
import com.mohamed.backend.topics.sub.SubTopic;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SessionDto {
    private Integer id;
    private LocalDate date;
    private SubTopic subTopic;
    private boolean cancelled;

    @JsonProperty("class")
    private Class semesterClass;
}
