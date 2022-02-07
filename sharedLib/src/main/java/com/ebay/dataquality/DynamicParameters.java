package com.ebay.dataquality;

import com.ebay.dataquality.exception.JsonConvertException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class DynamicParameters {
    public KeyValueParameters toKV() {
        try {
            ObjectMapper om = DefaultObjectMapper.getObjectMapper();
            byte[] bytes = om.writeValueAsBytes(this);
            return om.readValue(bytes, KeyValueParameters.class);
        } catch (IOException e) {
            throw new JsonConvertException("failed to convert json: " + e.getMessage(), e);
        }
    }
}
