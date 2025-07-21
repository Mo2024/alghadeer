package com.mohamed.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private String message;
    private Object object;
    private int status;

    public Response(String message) {
        this.message = message;
    }

    public Response(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public Response(String message, Object object) {
        this.message = message;
        this.object = object;
    }
}