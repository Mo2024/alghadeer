package com.mohamed.backend.salah.attempt.dto;

import com.mohamed.backend.salah.level.StudentLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LatestAttemptsWithStudentLevelDto {
    private List<SalahAttemptView> latestAttempts;
    private StudentLevel studentLevel;
}
