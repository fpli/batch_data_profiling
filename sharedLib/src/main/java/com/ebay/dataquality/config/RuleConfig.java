package com.ebay.dataquality.config;

import com.ebay.dataquality.KeyValueParameters;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
/*
@JsonSerialize(using = RuleConfig.Serializer.class)
@JsonDeserialize(using = RuleConfig.Deserializer.class)
*/
public class RuleConfig {

    @JsonProperty(index = 0)
    String name;

    @JsonProperty(index = 1)
    String type;

    @JsonProperty(index = 2)
    KeyValueParameters config;

    @JsonProperty(index = 3)
    @Singular
    List<ProfilerConfig> profilers;

    /*
    @JsonProperty(index = 2) private DynamicParameters dynamicConfig;
    static class Serializer extends StdSerializer<RuleConfig> {
        protected Serializer(){
            this(null);
        }

        protected Serializer(Class<RuleConfig> t) {
            super(t);
        }

        @Override
        public void serialize(RuleConfig value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("name", value.name);
            gen.writeStringField("type", value.type);

            Optional<TypeReference<? extends DynamicParameters>> sf = DynamicParametersRegistry.RULE.getByType(value.type);
            if ( !sf.isPresent() ) throw new IllegalArgumentException("unknown RULE type: " + value.type);
            gen.writeObjectField("config", value.dynamicConfig);
            gen.writeObjectField("profilers", value.profilers);
            gen.writeEndObject();
        }
    }

    static class Deserializer extends StdDeserializer<RuleConfig> {
        protected Deserializer(){
            this(null);
        }

        protected Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public RuleConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode rootNode = p.getCodec().readTree(p);
            String type = rootNode.get("type").textValue();
            String name = rootNode.get("name").textValue();
            JsonNode profilersNode = rootNode.get("profilers");
            if ( profilersNode == null ) {
                throw new IllegalArgumentException("RULE: profilers is mandatory");
            }
            List<ProfilerConfig> profilers = profilersNode.traverse(p.getCodec()).readValueAs(new TypeReference<List<ProfilerConfig>>(){});

            JsonNode valueNode = rootNode.get("config");
            if ( valueNode == null ) throw new IllegalArgumentException("RULE: config is mandatory.");

            Optional<TypeReference<? extends DynamicParameters>> sf = DynamicParametersRegistry.RULE.getByType(type);
            if ( !sf.isPresent() ) throw new IllegalArgumentException("RULE: unknown type: " + type);

            DynamicParameters cc = valueNode.traverse(p.getCodec()).readValueAs(sf.get());
            return RuleConfig.builder().dynamicConfig(cc).name(name).type(type).profilers(profilers).build();
        }
    }
     */
}
