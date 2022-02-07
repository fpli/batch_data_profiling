package com.ebay.dataquality.metric.exception;

public abstract class MetricBaseException extends RuntimeException {
    public MetricBaseException() {
    }

    public MetricBaseException(String message) {
        super(message);
    }

    public MetricBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetricBaseException(Throwable cause) {
        super(cause);
    }

    public MetricBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
