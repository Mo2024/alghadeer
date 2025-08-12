package com.mohamed.backend.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.topics.SubTopic;
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
