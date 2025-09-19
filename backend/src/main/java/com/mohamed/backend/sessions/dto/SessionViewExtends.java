package com.mohamed.backend.sessions.dto;

import com.mohamed.backend.topics.sub.SubTopic;

import java.util.List;

public interface SessionViewExtends extends SessionView {
    List<SubTopic> getSubTopics();

    boolean getCancelled();

}
