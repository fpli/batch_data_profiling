package com.ebay.dataquality.profiling.source

import com.ebay.dataquality.DynamicParameters
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.DataFrame

trait SQLSource[T <: DynamicParameters with HasSQL] {
  self: BatchSource[T] =>
  self.converts += convert

  final def convert(df: DataFrame): DataFrame = {
    val sql = self.getConfig.getSQL()
    val name = self.getConfig.getTableName()
    if (StringUtils.isEmpty(sql)) df else {
      if (sql.contains("${table_name}") && StringUtils.isEmpty(name)) {
        throw new IllegalArgumentException("${table_name} is used but not defined in configuraiton.")
      }
      df.createTempView(name)
      df.sqlContext.sql(sql.replace("${table_name}", name))
    }
  }
}
