package com.ebay.dataquality.config;

import com.ebay.dataquality.KeyValueParameters;
import lombok.*;

import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ColumnOperators {

    String id;
    String columnName;
    String columnType;
    List<KeyValueParameters> operators;
}
