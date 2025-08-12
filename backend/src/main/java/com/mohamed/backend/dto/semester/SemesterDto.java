package com.mohamed.backend.dto.semester;

import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.enums.SemesterList;
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
