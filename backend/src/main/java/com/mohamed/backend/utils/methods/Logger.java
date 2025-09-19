package com.mohamed.backend.utils.methods;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Logger {

    private final ObjectMapper mapper;

    public void logJsonObject(String logMessage, Object object) throws JsonProcessingException {
        try {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(object);
            log.info(logMessage, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object for logging: {}", logMessage, e);
        }
    }

    public void logJsonObjectError(String logMessage, Object object) throws JsonProcessingException {
        try {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(object);
            log.error(logMessage, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object for logging: {}", logMessage, e);
        }
    }

}
