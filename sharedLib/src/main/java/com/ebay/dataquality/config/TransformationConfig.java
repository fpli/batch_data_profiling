package com.ebay.dataquality.config;

import lombok.*;

@Builder
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TransformationConfig {

    String alias;

    ExpressionConfig expression;

}
