package com.ebay.dataquality.config;

import com.ebay.dataquality.KeyValueParameters;
import lombok.*;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
/*
@JsonSerialize(using = ExpressionConfig.Serializer.class)
@JsonDeserialize(using = ExpressionConfig.Deserializer.class)
*/
public class ExpressionConfig {

    String operator;
    KeyValueParameters config;

    //private DynamicParameters dynamicConfig;

    /*
    static class Serializer extends StdSerializer<ExpressionConfig> {
        protected Serializer(){
            this(null);
        }

        protected Serializer(Class<ExpressionConfig> t) {
            super(t);
        }

        @Override
        public void serialize(ExpressionConfig value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("operator", value.operator);
            Optional<TypeReference<? extends DynamicParameters>> sf = DynamicParametersRegistry.EXPRESSION.getByType(value.operator);
            if ( !sf.isPresent() ) throw new IllegalArgumentException("unknown operator: " + value.operator);
            gen.writeObjectField("params", value.dynamicConfig);
            gen.writeEndObject();
        }
    }

    static class Deserializer extends StdDeserializer<ExpressionConfig> {
        protected Deserializer(){
            this(null);
        }

        protected Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public ExpressionConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            String operator = node.get("operator").textValue();

            JsonNode valueNode = node.get("params");
            if ( valueNode == null ) throw new IllegalArgumentException("params is mandatory.");

            Optional<TypeReference<? extends DynamicParameters>> sf = DynamicParametersRegistry.EXPRESSION.getByType(operator);
            if ( !sf.isPresent() ) throw new IllegalArgumentException("unknown operator: " + operator);

            DynamicParameters cc = valueNode.traverse(p.getCodec()).readValueAs(sf.get());
            return ExpressionConfig.builder().dynamicConfig(cc).operator(operator).build();
        }
    }
    */
}
