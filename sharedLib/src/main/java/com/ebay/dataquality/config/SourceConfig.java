package com.ebay.dataquality.config;

import com.ebay.dataquality.KeyValueParameters;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SourceConfig {

    @JsonProperty(index = 0)
    String name;

    @JsonProperty(index = 1)
    String type;

    @JsonProperty(index = 2)
    KeyValueParameters config;

    /*
    @JsonProperty(index = 2)
    private DynamicParameters dynamicConfig;

    static class Serializer extends StdSerializer<SourceConfig>{
        protected Serializer(){
            this(null);
        }

        protected Serializer(Class<SourceConfig> t) {
            super(t);
        }

        @Override
        public void serialize(SourceConfig value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("name", value.getName());
            gen.writeStringField("type", value.getType());
            Optional<TypeReference<? extends DynamicParameters>> sf = DynamicParametersRegistry.SOURCE.getByType(value.getType());
            if ( !sf.isPresent() ) throw new IllegalArgumentException("SOURCE: unknown type: " + value.getType());
            gen.writeObjectField("config", value.dynamicConfig);
            gen.writeEndObject();
        }
    }

    static class Deserializer extends StdDeserializer<SourceConfig> {
        protected Deserializer(){
            this(null);
        }

        protected Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public SourceConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            String strType = node.get("type").textValue();
            String name = node.get("name").textValue();

            JsonNode valueNode = node.get("config");
            if ( valueNode == null ) throw new IllegalArgumentException("config is mandatory.");

            Optional<TypeReference<? extends DynamicParameters>> sf = DynamicParametersRegistry.SOURCE.getByType(strType);
            if ( !sf.isPresent() ) throw new IllegalArgumentException("SOURCE: unknown type: " + strType);

            DynamicParameters cc = valueNode.traverse(p.getCodec()).readValueAs(sf.get());
            return SourceConfig.builder().dynamicConfig(cc).name(name).type(strType).build();
        }
    }
    */
}
