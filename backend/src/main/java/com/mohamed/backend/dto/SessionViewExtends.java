package com.mohamed.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamed.backend.model.topics.SubTopic;

public interface SessionViewExtends extends SessionView {
    SubTopic getSubTopic();

    boolean getCancelled();
    
}
