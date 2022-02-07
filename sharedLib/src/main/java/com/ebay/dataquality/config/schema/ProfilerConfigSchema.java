package com.ebay.dataquality.config.schema;

import lombok.*;

import java.util.List;

@ToString
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ProfilerConfigSchema {
    //private List<String> supportedProfilers;

    private String name;
    @Singular
    private List<ConfigParamSchema> requiredParams;
    @Singular
    private List<ConfigParamSchema> optionalParams;
}