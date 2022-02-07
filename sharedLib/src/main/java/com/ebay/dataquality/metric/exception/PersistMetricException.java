package com.ebay.dataquality.metric.exception;

public class PersistMetricException extends MetricBaseException {
    public PersistMetricException(String message) {
        super(message);
    }

    public PersistMetricException(String message, Throwable cause) {
        super(message, cause);
    }
}
