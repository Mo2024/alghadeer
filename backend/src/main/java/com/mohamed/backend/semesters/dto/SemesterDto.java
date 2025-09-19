package com.mohamed.backend.semesters.dto;

import com.mohamed.backend.classes.Class;
import com.mohamed.backend.semesters.SemesterList;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class SemesterDto {
    private Integer id;

    private String name;

    private SemesterList semester;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;

    private List<Class> classes = new ArrayList<>();

    private boolean defaultClasses;

}
