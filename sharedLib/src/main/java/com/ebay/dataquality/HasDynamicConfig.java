package com.ebay.dataquality;

import com.fasterxml.jackson.core.type.TypeReference;

public interface HasDynamicConfig<T extends DynamicParameters> {

    void setConfig(KeyValueParameters config);

    TypeReference<T> getConfigType();

}
