package com.ebay.dataquality.exception;

public class RequiredParameterException extends ValidationException {
    private String parameterName;
    private String configName;

    public RequiredParameterException(String parameterName, String configName) {
        super("");
        this.parameterName = parameterName;
        this.configName = configName;
    }

    @Override
    public String getMessage() {
        return "Required parameter '" + parameterName + "' missed for " + configName;
    }
}
