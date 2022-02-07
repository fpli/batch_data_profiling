package com.ebay.dataquality.config.params.transform;

import com.ebay.dataquality.DynamicParameters;
import lombok.*;

import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SumParameters extends DynamicParameters {
    @Getter
    @Singular
    List<String> columns;
}
