package com.mohamed.backend.dto;

import com.mohamed.backend.model.user.StaffPermission;
import java.util.List;


public interface StaffView {
    Long getId();
    String getName();
    List<StaffPermission> getPermissions();
}
