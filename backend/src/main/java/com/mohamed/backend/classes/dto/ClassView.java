package com.mohamed.backend.classes.dto;

import com.mohamed.backend.classes.classesSchedules.ClassSchedule;

import java.util.List;

public interface ClassView {
    int getId();

    String getName();

    List<ClassSchedule> getClassSchedules();

}