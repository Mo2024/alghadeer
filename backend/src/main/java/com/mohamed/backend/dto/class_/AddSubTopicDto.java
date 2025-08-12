package com.mohamed.backend.dto.class_;

import lombok.Data;

import java.util.List;

@Data
public class AddSubTopicDto {

    private int sessionId;
    private List<Integer> subTopicsId;
}
