package com.ebay.dataquality.config.params;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DynamicConfigType {

    String type;

    //TODO: defines schema of the config of this type.
    //private String schema;
}
