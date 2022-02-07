package com.ebay.dataquality.profiling.source

import com.ebay.dataquality.DynamicParameters
import com.ebay.dataquality.profiling.source.BatchSourceFactory._
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.spark.sql.{DataFrame, SparkSession}

case class CsvConfig(header: Boolean, file: String, columns: Seq[String], sql: String, dateFilter: DateFilter, @JsonProperty("table-name") tableName: String) extends DynamicParameters with HasSQL {
  override def getSQL() = sql

  override def getTableName(): String = tableName
}

class CsvSource extends BatchSource[CsvConfig] with SQLSource[CsvConfig] with DateFilterSupport {

  override def initDf(session: SparkSession): DataFrame = {
    val config = this.getConfig
    val df = session.read.option("header", config.header).option("inferSchema", true).csv(config.file)

    val toDF = if (config.columns != null && config.columns.length > 0) {
      this.dateFilter = config.dateFilter
      if (dateFilter != null) {
        filterWithDate(df.toDF(config.columns: _*), this.context("launchTime"))
      } else {
        df.toDF(config.columns: _*)
      }
    } else {
      df
    }
    toDF
  }

}
