package com.ebay.dataquality.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.util.Optional;

@Getter
@Builder
@ToString
@JsonSerialize(using = ConfigParam.Serializer.class)
@JsonDeserialize(using = ConfigParam.Deserializer.class)
public class ConfigParam {

    private String key;
    private Optional<ConfigParamValue> value;

    @JsonIgnore
    public ConfigParamValue getOrThrow() {
        if (this.value.isPresent()) return this.value.get();
        else throw new IllegalArgumentException("value does't exist for key " + key);
    }

    // Json serializer of ConfigParam
    static class Serializer extends StdSerializer<ConfigParam> {
        protected Serializer() {
            this(null);
        }

        protected Serializer(Class<ConfigParam> t) {
            super(t);
        }

        @Override
        public void serialize(ConfigParam value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeObjectField("key", value.key);
            Optional<ConfigParamValue> optValue = value.getValue();
            if (optValue.isPresent()) {
                gen.writeObjectField("value", optValue.get());
            }
            gen.writeEndObject();
        }
    }

    static class Deserializer extends StdDeserializer<ConfigParam> {
        protected Deserializer() {
            this(null);
        }

        protected Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public ConfigParam deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            JsonNode keyNode = node.get("key");
            //ConfigParamKey key = keyNode.traverse(p.getCodec()).readValueAs(ConfigParamKey.class);
            String key = keyNode.asText();

            Optional<ConfigParamValue> optValue = Optional.empty();
            JsonNode valueNode = node.get("value");
            if (valueNode != null) {
                ConfigParamValue value = valueNode.traverse(p.getCodec()).readValueAs(ConfigParamValue.class);
                optValue = Optional.of(value);
            }
            return new ConfigParam(key, optValue);
        }
    }
}

