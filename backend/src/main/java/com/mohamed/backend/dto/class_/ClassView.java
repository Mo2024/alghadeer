package com.mohamed.backend.dto.class_;

import com.mohamed.backend.model.classinfo.ClassSchedule;

import java.util.List;

public interface ClassView {
    int getId();

    String getName();

    List<ClassSchedule> getClassSchedules();

}