package com.mohamed.backend.dto.semester;

import java.time.LocalDateTime;

public interface AnnouncementView {
    Integer getId();

    String getContent();

    String getAnnouncementType();

    LocalDateTime getStartDate();

    LocalDateTime getEndDate();

    LocalDateTime getCreatedAt();

    boolean getIsGeneral();

    boolean getIsCancelled();
}
