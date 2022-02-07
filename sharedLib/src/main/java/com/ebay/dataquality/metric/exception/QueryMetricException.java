package com.ebay.dataquality.metric.exception;

public class QueryMetricException extends MetricBaseException {
    public QueryMetricException(String message) {
        super(message);
    }

    public QueryMetricException(String message, Throwable cause) {
        super(message, cause);
    }
}
