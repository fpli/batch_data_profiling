package com.ebay.dataquality.config;

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
import lombok.ToString;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@ToString
@JsonSerialize(using = ConfigParamValue.Serializer.class)
@JsonDeserialize(using = ConfigParamValue.Deserializer.class)
public class ConfigParamValue {

    private ConfigParamValueTypeEnum type;
    private Object value;

    public ConfigParamValue(ConfigParamValueTypeEnum type, Object value) {
        this.type = type;
        setValue(value);
    }

    public ConfigParamValueTypeEnum getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    final public void setValue(Object value) {
        if (!type.getTypeClz().isInstance(value)) typeThrow(type.getTypeClz().getName(), value.getClass().getName());
        this.value = type.getTypeClz().cast(value);
    }

    public int getInt() {
        return (Integer) value;
    }

    public String getString() {
        return (String) value;
    }

    public double getDouble() {
        return (Double) value;
    }

    public boolean getBoolean() {
        return (Boolean) value;
    }

    private void typeThrow(String expectedType, String actualType) {
        throw new IllegalArgumentException("Expect type: " + expectedType + " actual " + actualType);
    }

    public static Optional<ConfigParamValue> fromValue(Object value) {
        if (null == value) return Optional.empty();
        Optional<ConfigParamValueTypeEnum> type = Arrays.stream(ConfigParamValueTypeEnum.values()).filter(v -> v.getTypeClz().isInstance(value)).findFirst();
        return type.map(t -> new ConfigParamValue(type.get(), value));
    }

    // serializer class of ConfigParamValue
    static class Serializer extends StdSerializer<ConfigParamValue> {
        public Serializer() {
            this(null);
        }

        public Serializer(Class<ConfigParamValue> t) {
            super(t);
        }

        @Override
        public void serialize(ConfigParamValue value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("type", value.getType().name());
            switch (value.getType()) {
                case Integral:
                    gen.writeNumberField("value", value.getInt());
                    break;
                case Double:
                    gen.writeNumberField("value", value.getDouble());
                    break;
                case Boolean:
                    gen.writeBooleanField("value", value.getBoolean());
                    break;
                case String:
                    gen.writeStringField("value", value.getString());
                    break;
                default:
                    throw new IllegalArgumentException("invalid type" + value.getType());
            }

            gen.writeEndObject();
        }
    }

    static class Deserializer extends StdDeserializer<ConfigParamValue> {

        protected Deserializer() {
            this(null);
        }

        protected Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public ConfigParamValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            String strType = node.get("type").textValue();
            ConfigParamValueTypeEnum type = ConfigParamValueTypeEnum.valueOf(strType);
            if (null == type) throw new IllegalArgumentException("Unsupported type: " + strType);

            JsonNode valueNode = node.get("value");
            if (valueNode == null) throw new IllegalArgumentException("value is mandatory.");
            switch (type) {
                case Integral:
                    return new ConfigParamValue(type, valueNode.asInt());
                case Double:
                    return new ConfigParamValue(type, valueNode.asDouble());
                case Boolean:
                    return new ConfigParamValue(type, valueNode.asBoolean());
                case String:
                    return new ConfigParamValue(type, valueNode.asText());
                default:
                    throw new IllegalArgumentException("Unsupported type: " + strType);
            }
        }
    }
}
