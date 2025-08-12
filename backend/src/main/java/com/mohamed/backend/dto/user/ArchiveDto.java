package com.mohamed.backend.dto.user;

import com.mohamed.backend.model.user.Staff;
import lombok.Data;

@Data
public class ArchiveDto {
    private Staff staff;
    private int page;
    private int size;

}
