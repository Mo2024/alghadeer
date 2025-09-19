package com.mohamed.backend.classes.dto;

import com.mohamed.backend.sessions.dto.SessionViewNested;
import com.mohamed.backend.assignments.Assignment;

import java.util.List;

public interface ClassView2 extends ClassView {
    List<SessionViewNested> getSessions();

    List<Assignment> getAssignments();
}
