package com.ebay.dataquality.config.params;

import com.ebay.dataquality.DynamicParameters;
import com.fasterxml.jackson.core.type.TypeReference;

public interface HasDynamicConfig<T extends DynamicParameters> {

    void setConfig(KeyValueParameters config);

    TypeReference<T> getConfigType();
}
