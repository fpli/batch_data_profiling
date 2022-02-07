package com.ebay.dataquality.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@ToString(callSuper = true)
public class JErrorMetric extends JMetric<String> {

    private JErrorMetric() {
    }

    @Getter
    @JsonProperty("ErrorValue")
    protected String value;

    public JErrorMetric(String msg) {
        this.value = msg;
    }

    @Override
    public List<JDoubleMetric> flatten() {
        return Collections.emptyList();
    }
}
