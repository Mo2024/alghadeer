package com.mohamed.backend.users.staff.dto;

import com.mohamed.backend.users.staff.Staff;
import lombok.Data;

@Data
public class ArchiveDto {
    private Staff staff;
    private int page;
    private int size;

}
