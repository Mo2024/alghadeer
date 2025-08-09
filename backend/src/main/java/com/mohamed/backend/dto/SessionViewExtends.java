package com.mohamed.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.model.topics.SubTopic;
import com.mohamed.backend.repository.classinfo.SessionRepository;

public interface SessionViewExtends extends SessionRepository {
    SubTopic getSubTopic();

    boolean getCancelled();

    @JsonProperty("class")
    ClassView getSemesterClass();
}
