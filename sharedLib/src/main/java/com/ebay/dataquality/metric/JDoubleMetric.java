package com.ebay.dataquality.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ToString(callSuper = true)
public class JDoubleMetric extends JMetric<Double> {

    private JDoubleMetric() {
    }

    @Getter
    @JsonProperty("DoubleValue")
    protected Double value;

    public JDoubleMetric(/*String name, String instance,*/ Double value) {
        /*
        this.name = name;
        this.instance = instance;
        */
        this.value = value;
    }

    @Override
    public List<JDoubleMetric> flatten() {
        return Collections.singletonList(this);
    }
}
