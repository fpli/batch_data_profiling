package com.ebay.dataquality.config;

import com.ebay.dataquality.KeyValueParameters;
import lombok.*;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SinkConfig {
    String name;
    String type;
    KeyValueParameters config;
}
