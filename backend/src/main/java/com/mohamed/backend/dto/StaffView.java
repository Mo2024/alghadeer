package com.mohamed.backend.dto;

import com.mohamed.backend.model.user.StaffPermission;
import java.util.List;


public interface StaffView {
    int getId();
    String getName();
    List<StaffPermission> getPermissions();
}
