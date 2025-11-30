package com.mohamed.backend.salah.attempt.dto;

public interface SalahAttemptView {

    Long getId();

    Integer getSubjectId();

    java.time.LocalDateTime getLatestAttempt();

    Boolean getCompleted();

    Boolean getPassed();

    String getSubjectName();
}
