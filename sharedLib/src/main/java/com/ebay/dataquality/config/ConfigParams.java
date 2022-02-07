package com.ebay.dataquality.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigParams {

    @Getter
    @Singular("param")
    private List<ConfigParam> paramList = new ArrayList<>();

    @JsonIgnore
    private Map<String, ConfigParam> paramMap = null;

    @JsonIgnore
    public Optional<ConfigParam> getByKey(String key) {
        if (null == paramMap) {
            paramMap = paramList.stream().collect(Collectors.toMap(ConfigParam::getKey, Function.identity()));
        }
        return Optional.ofNullable(paramMap.get(key));
    }
}
