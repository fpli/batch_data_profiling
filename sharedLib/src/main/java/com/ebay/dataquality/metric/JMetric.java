package com.ebay.dataquality.metric;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.ToString;

import java.util.List;

@ToString
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "metricType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JDoubleMetric.class, name = "JDoubleMetric"),
        @JsonSubTypes.Type(value = JKeyedDoubleMetric.class, name = "JKeyedDoubleMetric"),
        @JsonSubTypes.Type(value = JErrorMetric.class, name = "JErrorMetric")
})
abstract public class JMetric<T> {
    //@Getter protected T value;
    abstract protected T getValue();
    /*
    @Getter protected String name;
    @Getter protected String instance;
    */

    public abstract List<JDoubleMetric> flatten();

    /*
    public static class JMetricSerializer extends StdSerializer<JMetric>{
        public JMetricSerializer(){
            this(null);
        }

        public JMetricSerializer(Class<JMetric> t) {
            super(t);
        }

        @Override
        public void serialize(JMetric value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            String metricType = null;
            if ( value instanceof JDoubleMetric ) {
                metricType = "JDoubleMetric";
            }

            if ( value instanceof JKeyedDoubleMetric ) {
                metricType = "JKeyedDoubleMetric";
            }

            if ( null == metricType )
                throw new IllegalArgumentException("Unknown JMetric class: " + value.getClass().getCanonicalName());

            gen.writeStartObject();
            gen.writeStringField("metricType", metricType);
            gen.writeStringField("name", value.name);
            gen.writeStringField("instance", value.instance);
            gen.writeObjectField("value", value.getValue());
            gen.writeEndObject();
        }
    }

    public static class JMetricDeserializer extends StdDeserializer<JMetric> {

        protected JMetricDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public JMetric deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);

            String metricType = node.get("metricType").textValue();
            if ( null == metricType ) throw new RequiredParameterException("metricType", "metric");

            switch(metricType) {
                case "JDoubleMetric":
                    return p.getCodec().readValue(p, JDoubleMetric.class);
                case "JKeyedDoubleMetric":
                    return p.getCodec().readValue(p, JKeyedDoubleMetric.class);
                default:
                    throw new TypeUnmatchException("Unknown metric type: " + metricType);
            }
        }
    }
    */
}