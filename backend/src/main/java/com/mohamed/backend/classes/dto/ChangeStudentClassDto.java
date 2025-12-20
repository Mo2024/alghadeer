package com.mohamed.backend.classes.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChangeStudentClassDto {
    private List<Integer> studentsId;
    private int classId;
}
