package com.mohamed.backend.dto.user;

import lombok.Data;

@Data
public class UpdatePasswordDto {
    private String currentPassword;
    private String newPassword;
    private String newPassword2;
}
