package com.mohamed.backend.semesters.dto;

import java.time.LocalDateTime;

public interface StudentExportDto {
    String getName();
    String getCpr();
    String getTelephone1();
    String getTelephone2();
    String getClassName();
    LocalDateTime getEnrollmentDate();
}
