package com.ebay.dataquality;

import com.ebay.dataquality.config.SourceConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
public class ProfilerConfigsV1 {

    private String name;

    @Singular
    private List<SourceConfig> datasources;

    @Singular("config")
    private List<ProfilerConfigV1> configList;

    public String toJson() {
        try {
            return DefaultObjectMapper.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to write json: " + e.getMessage(), e);
        }
    }

    public static ProfilerConfigsV1 fromJson(String json) {
        try {
            return DefaultObjectMapper.getObjectMapper().readValue(json, ProfilerConfigsV1.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to read json: " + e.getMessage(), e);
        }
    }

    public static ProfilerConfigsV1 fromURL(URL url) {
        try {
            return DefaultObjectMapper.getObjectMapper().readValue(url, ProfilerConfigsV1.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to read json: " + e.getMessage(), e);
        }
    }
}
