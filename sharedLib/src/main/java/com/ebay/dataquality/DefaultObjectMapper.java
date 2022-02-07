package com.ebay.dataquality;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

public class DefaultObjectMapper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return DefaultObjectMapper.getObjectMapper().readValue(json, classOfT);
        } catch (JsonProcessingException e) {
            String msg = String.format("failed to read json from content for class %s, message: %s", classOfT.getSimpleName(), e.getMessage());
            throw new IllegalArgumentException(msg, e);
        }
    }

    public static <T> T fromURL(URL url, Class<T> classOfT) {
        try {
            return DefaultObjectMapper.getObjectMapper().readValue(url, classOfT);
        } catch (IOException e) {
            String msg = String.format("failed to read json from %s for class %s, message: %s", url.toString(), classOfT.getSimpleName(), e.getMessage());
            throw new IllegalArgumentException(msg, e);
        }
    }

    public static String toJson(Object o) {
        try {
            return DefaultObjectMapper.getObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            String msg = String.format("failed to convert object of type %s into json, message: %s", o.getClass().getSimpleName(), e.getMessage());
            throw new IllegalArgumentException(msg, e);
        }
    }
}