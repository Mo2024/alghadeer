package com.mohamed.backend.utils.exceptions;

public class HandledRejection extends RuntimeException {
    public HandledRejection(String message) {
        super(message);
    }
}