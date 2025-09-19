package com.mohamed.backend.users.staff.dto;

import com.mohamed.backend.users.staff.staffPermissions.StaffPermission;

import java.util.List;


public interface StaffView {
    int getId();

    String getName();

    List<StaffPermission> getPermissions();
}
