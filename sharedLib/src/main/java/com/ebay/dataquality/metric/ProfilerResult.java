package com.ebay.dataquality.metric;

import com.ebay.dataquality.DefaultObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * Entity of profiler result. Basically one profiler result denotes one metric.
 * <p>
 * It contains below properties:
 * - timestamp -- when the result is generated
 * - name -- name of the result
 * -- configName -- profiler result is generated based on one user's configuration, config name identifies which config it is
 * - operator -- what operator applied to the original dataset to generate this profiler result.
 * - tags -- dynamic label/value pairs of this result, for example {dataset: user_table, column: age} denotes this result is related with user_table and age column.
 * - metric -- the value of this result.
 * - from -- start of timestamp used to generate the metric
 * - to -- end of timestamp used to generate the metric
 * <p>
 * version property is defined here for future extension.
 */
@Data
//@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Builder
public class ProfilerResult {

    @Builder.Default
    private final String version = "1.0";
    private final String configName;
    private final long timestamp;
    @Builder.Default
    private final long from = -1;
    @Builder.Default
    private final long to = -1;

    //metric name
    private final String name;
    private final String operator;

    @Singular
    private final Map<String, String> tags;

    private final JMetric<? extends Object> metric;

    public static String toJson(List<ProfilerResult> results) {
        ObjectMapper mapper = DefaultObjectMapper.getObjectMapper();
        try {
            return mapper.writeValueAsString(results);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to write json " + e.getMessage(), e);
        }
    }

    public static String toJson(ProfilerResult result) {
        ObjectMapper mapper = DefaultObjectMapper.getObjectMapper();
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to write json " + e.getMessage(), e);
        }
    }

    public static ProfilerResult fromJsonText(String json) {
        ObjectMapper mapper = DefaultObjectMapper.getObjectMapper();
        try {
            return mapper.readValue(json, ProfilerResult.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to read json: " + e.getMessage(), e);
        }
    }

    public static List<ProfilerResult> fromJsonList(String jsonList) {
        ObjectMapper mapper = DefaultObjectMapper.getObjectMapper();
        try {
            return mapper.readValue(jsonList, new TypeReference<List<ProfilerResult>>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to read json: " + e.getMessage(), e);
        }
    }
}
