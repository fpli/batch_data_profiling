package com.ebay.dataquality;

import com.ebay.dataquality.config.ConfigParam;
import com.ebay.dataquality.config.ConfigParamValue;
import com.ebay.dataquality.config.ConfigParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * config of one profiler --- each config will generate one metric
 */
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
public class ProfilerConfigV1 {
    // config name (or you can consider it as metric name, should be unique within one data source)
    private String name;

    private String profiler;
    private ConfigParams params;

    public Optional<ConfigParam> getParamByKey(String key) {
        return params.getByKey(key);
    }

    public ConfigParam byKeyOrThrow(String key) {
        Optional<ConfigParam> option = params.getByKey(key);
        if (option.isPresent()) return option.get();
        throw new IllegalArgumentException("required key " + key + " doesn't exist.");
    }

    public String toJson() {
        try {
            return DefaultObjectMapper.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to write json: " + e.getMessage(), e);
        }
    }

    public static ProfilerConfigV1 fromJson(String json) {
        try {
            return DefaultObjectMapper.getObjectMapper().readValue(json, ProfilerConfigV1.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to read json: " + e.getMessage(), e);
        }
    }

    public static ProfilerConfigV1 newInstance(String name, String profiler, Object... params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid params, required and must be pairs.");
        }
        int index = 0;
        List<ConfigParam> paramList = new ArrayList();
        while (index < params.length) {
            Object objKey = params[index++];
            Object objValue = params[index++];
            if (!(objKey instanceof String)) {
                throw new IllegalArgumentException("Invalid key: " + objKey.toString() + ", expect String");
            }
            String key = (String) objKey;
            Optional<ConfigParamValue> optValue = ConfigParamValue.fromValue(objValue);
            paramList.add(ConfigParam.builder().key(key).value(optValue).build());
        }

        ConfigParams configParams = ConfigParams.builder().paramList(paramList).build();

        return ProfilerConfigV1.builder().name(name).profiler(profiler).params(configParams).build();
    }
}
