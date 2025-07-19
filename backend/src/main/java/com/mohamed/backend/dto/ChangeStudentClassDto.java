package com.mohamed.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChangeStudentClassDto {
    private List<Integer> studentsId;
    private int classId;
}
