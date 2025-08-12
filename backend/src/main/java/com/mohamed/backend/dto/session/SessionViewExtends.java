package com.mohamed.backend.dto.session;

import com.mohamed.backend.model.topics.SubTopic;

import java.util.List;

public interface SessionViewExtends extends SessionView {
    List<SubTopic> getSubTopics();

    boolean getCancelled();

}
