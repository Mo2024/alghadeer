package com.mohamed.backend.dto;

import java.util.List;

public interface ClassView {
    int getId();
    String getName();
    List<SessionView> getSessions();
}