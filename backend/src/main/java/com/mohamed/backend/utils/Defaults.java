package com.mohamed.backend.utils;

import java.util.List;
import com.mohamed.backend.model.classinfo.Class;
import com.mohamed.backend.model.semester.Semester;

public class Defaults {

    public static List<Class> getDefaultClasses(Semester semester) {
        return List.of(
                Class.builder().name("الصف الأول/الثاني").semester(semester).build(),
                Class.builder().name("الصف الثالث").semester(semester).build(),
                Class.builder().name("الصف الرابع").semester(semester).build(),
                Class.builder().name("الصف الخامس").semester(semester).build(),
                Class.builder().name("الصف السادس").semester(semester).build(),
                Class.builder().name("الأول إعدادي").semester(semester).build(),
                Class.builder().name("الثاني إعدادي").semester(semester).build(),
                Class.builder().name("الثالث إعدادي").semester(semester).build(),
                Class.builder().name("الثانوي").semester(semester).build()
        );
    }
}
