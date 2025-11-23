package com.mohamed.backend.salah.attempt;

public interface SalahAttemptView {

    Long getId();

    Integer getSubjectId();

    java.time.LocalDateTime getLatestAttempt();

    Boolean getCompleted();

    Boolean getPassed();

    String getSubjectName();
}
