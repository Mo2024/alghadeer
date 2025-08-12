package com.mohamed.backend.dto.class_;

import lombok.Data;

import java.util.List;

@Data
public class ChangeStudentClassDto {
    private List<Integer> studentsId;
    private int classId;
}
