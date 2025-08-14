package com.mohamed.backend.dto.class_;

import com.mohamed.backend.dto.session.SessionViewNested;
import com.mohamed.backend.model.classinfo.assignment.Assignment;

import java.util.List;

public interface ClassView2 extends ClassView {
    List<SessionViewNested> getSessions();

    List<Assignment> getAssignments();
}
