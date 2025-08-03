package com.mohamed.backend.dto;

import com.mohamed.backend.model.topics.SubTopic;
import java.time.LocalDate;

public interface SessionView {
    Integer getId();
    LocalDate getDate();
    SubTopic getSubTopic();
    boolean getCancelled();
    ClassView getSemesterClass();
}
