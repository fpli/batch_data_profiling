package com.ebay.dataquality;

import com.fasterxml.jackson.core.type.TypeReference;

public class BaseObjectHasDynamicConfig<T extends DynamicParameters> implements HasDynamicConfig<T> {

    private TypeReference<T> type;
    private T config;
    private KeyValueParameters kvs;

    public BaseObjectHasDynamicConfig(TypeReference<T> t) {
        this.type = t;
    }

    public T getConfig() {
        return this.config;
    }

    public KeyValueParameters rawConfig() {
        return this.kvs;
    }

    @Override
    public TypeReference<T> getConfigType() {
        return this.type;
    }

    @Override
    public void setConfig(KeyValueParameters kvs) {
        this.kvs = kvs;
        this.config = kvs.toSpecificType(type);
    }
}
