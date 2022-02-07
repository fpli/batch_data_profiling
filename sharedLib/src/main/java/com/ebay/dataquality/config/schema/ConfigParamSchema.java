package com.ebay.dataquality.config.schema;


import com.ebay.dataquality.config.ConfigParamValueTypeEnum;
import lombok.*;

@ToString
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ConfigParamSchema {
    private String name;
    private ConfigParamValueTypeEnum type;
}
