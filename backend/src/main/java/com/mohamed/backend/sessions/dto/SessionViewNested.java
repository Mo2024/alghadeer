package com.mohamed.backend.sessions.dto;

import com.mohamed.backend.topics.sub.SubTopic;

import java.time.LocalDate;
import java.util.List;

public interface SessionViewNested {
    Integer getId();

    LocalDate getDate();

    List<SubTopic> getSubTopics();

    boolean getCancelled();
}
