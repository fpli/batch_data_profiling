package com.ebay.dataquality.config.params.profiler;

import com.ebay.dataquality.DynamicParameters;
import lombok.*;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UniquenessParameters extends DynamicParameters {
    String column;
}
