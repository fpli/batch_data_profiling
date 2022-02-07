package com.ebay.dataquality;

import com.ebay.dataquality.config.RuleConfig;
import com.ebay.dataquality.config.SinkConfig;
import com.ebay.dataquality.config.SourceConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProfilerPipelineConfigFusion {

    @JsonProperty(index = 0)
    String name;

    @JsonProperty(index = 1)
    @Singular
    List<SourceConfig> datasources;

    @JsonProperty(index = 2)
    @Singular
    List<RuleConfig> rules;

    @JsonProperty(index = 3)
    @Singular
    List<SinkConfig> sinks;

}