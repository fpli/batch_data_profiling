package com.ebay.dataquality.config.schema;

import com.ebay.dataquality.DefaultObjectMapper;
import com.ebay.dataquality.ProfilerConfigV1;
import com.ebay.dataquality.ProfilerConfigsV1;
import com.ebay.dataquality.config.ConfigParam;
import com.ebay.dataquality.config.ConfigParamValue;
import com.ebay.dataquality.exception.RequiredParameterException;
import com.ebay.dataquality.exception.TypeUnmatchException;
import com.ebay.dataquality.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Getter
@ToString
public class ProfilerConfigSchemas {
    @Singular
    private List<ProfilerConfigSchema> schemas;

    private volatile static ProfilerConfigSchemas systemOne = null;

    /**
     * It reads schema definiton JSON file from pre-defined URL and generate the
     * single source of schema definition which defines the official profilers we supported, constraints and parameters needed.
     */
    public static ProfilerConfigSchemas systemDefined() {
        if (null == systemOne) {
            URL url = ProfilerConfigSchemas.class.getClassLoader().getResource("schema/profiler_config_schemas.json");
            systemOne = ProfilerConfigSchemas.fromURL(url);
        }
        return systemOne;
    }

    /**
     * construct ProfilerConfigSchemas from the given json content
     */
    public static ProfilerConfigSchemas fromJson(String jsonText) {
        ObjectMapper mapper = DefaultObjectMapper.getObjectMapper();
        JavaType type = mapper.getTypeFactory().
                constructCollectionType(List.class, ProfilerConfigSchema.class);
        try {
            List<ProfilerConfigSchema> schemas = mapper.readValue(jsonText, type);
            return ProfilerConfigSchemas.builder().schemas(schemas).build();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to parse ProfilerConfigSchemas from json: " + e.getMessage(), e);
        }
    }

    public static ProfilerConfigSchemas fromURL(URL url) {
        ObjectMapper mapper = DefaultObjectMapper.getObjectMapper();
        JavaType type = mapper.getTypeFactory().
                constructCollectionType(List.class, ProfilerConfigSchema.class);
        try {
            List<ProfilerConfigSchema> schemas = mapper.readValue(url, type);
            return ProfilerConfigSchemas.builder().schemas(schemas).build();
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to parse ProfilerConfigSchemas from url " + url + ": " + e.getMessage(), e);
        }
    }

    public static String toJson(ProfilerConfigSchemas schemas) {
        ObjectMapper mapper = DefaultObjectMapper.getObjectMapper();
        JavaType type = mapper.getTypeFactory().
                constructCollectionType(List.class, ProfilerConfigSchema.class);
        try {
            return mapper.writerFor(type).writeValueAsString(schemas.schemas);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to write ProfilerConfigSchemas to string: " + e.getMessage(), e);
        }
    }

    public void validateProfilerConfigs(ProfilerConfigsV1 profileConfigs) {
        Map<String, ProfilerConfigSchema> name2Schema = schemas.stream().collect(Collectors.toMap(ProfilerConfigSchema::getName, Function.identity()));

        Stream<ProfilerConfigV1> configStream = profileConfigs.getConfigList().stream();
        //check name
        checkProfileName(name2Schema, configStream.map(c -> c.getProfiler()).collect(Collectors.toList()));

        configStream = profileConfigs.getConfigList().stream();
        configStream.forEach(c -> {
            ProfilerConfigSchema schema = name2Schema.get(c.getProfiler());
            validateProfileConfig(schema, c);
        });
    }

    private void validateProfileConfig(ProfilerConfigSchema schema, ProfilerConfigV1 c) {
        if (schema.getRequiredParams() != null) {
            for (ConfigParamSchema paramSchema : schema.getRequiredParams()) {
                validateConfigParam(paramSchema, c, true);
            }
        }

        if (schema.getOptionalParams() != null) {
            for (ConfigParamSchema paramSchema : schema.getOptionalParams()) {
                validateConfigParam(paramSchema, c, false);
            }
        }
    }

    private void validateConfigParam(ConfigParamSchema schema, ProfilerConfigV1 c, boolean required) {
        String paramName = schema.getName();
        Optional<ConfigParam> optParam = c.getParamByKey(paramName);
        if (!optParam.isPresent()) {
            if (required) throw new RequiredParameterException(paramName, c.getName());
            else return;
        }

        ConfigParamValue value = optParam.get().getValue().orElse(null);
        if (null == value) {
            if (required) throw new RequiredParameterException(paramName, c.getName());
            else return;
        }

        if (value.getType() != schema.getType())
            throw new TypeUnmatchException("Type unmatch for " + c.getName() + "." + paramName + ", expect: " + schema.getType() + ", actual: " + value.getType());
    }

    private void checkProfileName(Map<String, ProfilerConfigSchema> name2Schema, List<String> profileNames) {
        for (String profileName : profileNames) {
            if (!name2Schema.containsKey(profileName)) {
                throw new ValidationException("Unknown profile name: " + profileName);
            }
        }
    }
}
