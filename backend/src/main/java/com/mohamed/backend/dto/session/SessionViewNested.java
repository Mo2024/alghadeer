package com.mohamed.backend.dto.session;

import com.mohamed.backend.model.topics.SubTopic;

import java.time.LocalDate;
import java.util.List;

public interface SessionViewNested {
    Integer getId();

    LocalDate getDate();

    List<SubTopic> getSubTopics();

    boolean getCancelled();
}
