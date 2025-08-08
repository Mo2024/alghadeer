package com.mohamed.backend.dto;

import com.mohamed.backend.model.topics.SubTopic;

import java.time.LocalDate;

public interface SessionViewNested {
    Integer getId();

    LocalDate getDate();

    SubTopic getSubTopic();

    boolean getCancelled();
}
