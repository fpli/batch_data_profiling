package com.ebay.dataquality.config;

public enum ConfigParamValueTypeEnum {
    Integral(Integer.class), Double(Double.class), Boolean(Boolean.class), String(String.class);

    private Class clz;

    ConfigParamValueTypeEnum(Class clz) {
        this.clz = clz;
    }

    public Class getTypeClz() {
        return this.clz;
    }
}
