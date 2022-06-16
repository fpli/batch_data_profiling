package com.ebay.dataquality.profiling.source

import com.ebay.dataquality.DynamicParameters
import com.ebay.dataquality.profiling.source.BatchSourceFactory.tableConfig
import org.apache.spark.sql.{DataFrame, SparkSession}

case class TableConfig(tableName: String, dateFilter: DateFilter) extends DynamicParameters

class TableSource extends BatchSource[TableConfig] with DateFilterSupport {

  override def initDf(session: SparkSession): DataFrame = {
    if (this.getConfig.tableName == null) throw new IllegalArgumentException("tableName is missing in the config of datasource")
    val df = session.read.table(this.getConfig.tableName)

    this.dateFilter = this.getConfig.dateFilter

    if (dateFilter != null) {
      filterWithDate(df, this.context("launchTime"))
    } else {
      df
    }
  }

}
