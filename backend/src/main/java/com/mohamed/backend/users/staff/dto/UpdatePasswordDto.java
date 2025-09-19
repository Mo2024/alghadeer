package com.mohamed.backend.users.staff.dto;

import lombok.Data;

@Data
public class UpdatePasswordDto {
    private String currentPassword;
    private String newPassword;
    private String newPassword2;
}
