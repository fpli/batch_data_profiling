package com.ebay.dataquality.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ToString(callSuper = true)
public class JKeyedDoubleMetric extends JMetric<Map<String, Double>> {

    private JKeyedDoubleMetric() {

    }

    @Getter
    @JsonProperty("KeyedDoubleValue")
    protected Map<String, Double> value;

    public JKeyedDoubleMetric(/*String name, String instance,*/ Map<String, Double> value) {
        /*
        this.name = name;
        this.instance = instance;
        */
        this.value = value;
    }

    @Override
    public List<JDoubleMetric> flatten() {
        return this.value.entrySet().stream()
                //.map( e -> new JDoubleMetric(this.name + "-" + e.getKey(), instance, e.getValue()))
                .map(e -> new JDoubleMetric(e.getValue()))
                .collect(Collectors.toList());
    }
}
