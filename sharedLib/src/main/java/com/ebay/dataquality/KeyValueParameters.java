package com.ebay.dataquality;

import com.ebay.dataquality.exception.JsonConvertException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class KeyValueParameters extends HashMap<String, Object> {
    public <T extends DynamicParameters> T toSpecificType(TypeReference<T> t) throws JsonConvertException {
        try {
            ObjectMapper mapper = DefaultObjectMapper.getObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(this);
            T tObj = mapper.readValue(bytes, t);
            return tObj;
        } catch (IOException e) {
            throw new JsonConvertException("failed to convert JSON object: " + e.getMessage(), e);
        }
    }
}
