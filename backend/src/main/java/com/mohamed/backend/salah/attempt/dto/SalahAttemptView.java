package com.mohamed.backend.salah.attempt.dto;

import com.mohamed.backend.salah.questions.Level;

public interface SalahAttemptView {

    Long getId();

    Integer getSubjectId();

    java.time.LocalDateTime getLatestAttempt();

    Boolean getCompleted();

    Boolean getPassed();

    String getSubjectName();

    Level getLevel();
}
