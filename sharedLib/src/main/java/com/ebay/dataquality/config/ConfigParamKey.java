package com.ebay.dataquality.config;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class ConfigParamKey {
    /*
    private String name;
    @EqualsAndHashCode.Exclude private String displayName;
    @EqualsAndHashCode.Exclude private String description;

    public final static ConfigParamKey COLUMN = ConfigParamKey.builder().name("column").displayName("Column Name").description("name of column").build();
    public final static ConfigParamKey WHERE = ConfigParamKey.builder().name("where").displayName("Condition").description("filtering condition").build();
    public final static ConfigParamKey REGEX = ConfigParamKey.builder().name("pattern").displayName("Regular Expression").description("match pattern").build();
    public final static ConfigParamKey QUANTILES = ConfigParamKey.builder().name("quantiles").displayName("quantiles").description("quantiles").build();
    public final static ConfigParamKey RELATIVE_ERROR = ConfigParamKey.builder().name("relativeError").displayName("relativeError").description("relativeError").build();
    */
    public final static String COLUMN = "column";
    public final static String WHERE = "where";
    public final static String PATTERN = "pattern";
    public final static String QUANTILES = "quantiles";
    public final static String RELATIVE_ERROR = "relativeError";
}
