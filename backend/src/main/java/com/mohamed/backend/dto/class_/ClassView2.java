package com.mohamed.backend.dto.class_;

import com.mohamed.backend.dto.session.SessionViewNested;

import java.util.List;

public interface ClassView2 extends ClassView {
    List<SessionViewNested> getSessions();

}
