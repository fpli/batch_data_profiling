package com.ebay.dataquality.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProfilerConfig {

    @JsonProperty(value = "metric-name", index = 0)
    String metricName;

    @JsonProperty(index = 1)
    ExpressionConfig expression;

    @JsonProperty(index = 2)
    String filter;

    @JsonProperty(index = 3)
    @Singular
    List<TransformationConfig> transformations;

    @JsonProperty(index = 4)
    @Singular
    List<String> dimensions;

    @JsonProperty(index = 5)
    String comment;
}