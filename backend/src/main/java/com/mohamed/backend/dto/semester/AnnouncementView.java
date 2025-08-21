package com.mohamed.backend.dto.semester;

import java.time.LocalDateTime;

public interface AnnouncementView {
    Integer getId();

    String getContent();

    String getAnnouncementType();

    LocalDateTime getStartDateTime();

    LocalDateTime getEndDateTime();

    LocalDateTime getCreatedAt();

    boolean getIsGeneral();

    boolean getIsCancelled();
}
