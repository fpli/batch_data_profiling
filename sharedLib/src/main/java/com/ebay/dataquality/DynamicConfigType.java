package com.ebay.dataquality;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DynamicConfigType {
    private String type;

    //TODO: defines schema of the config of this type.
    //private String schema;
}
