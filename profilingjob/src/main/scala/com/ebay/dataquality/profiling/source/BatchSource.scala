package com.ebay.dataquality.profiling.source

import com.ebay.dataquality.util.Loggable
import com.ebay.dataquality.{BaseObjectHasDynamicConfig, DynamicParameters}
import com.fasterxml.jackson.core.`type`.TypeReference
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable

/*
abstract class BatchSource[A <: DynamicParameters](t: TypeReference[A]) extends BaseSource[A](t){
  def getDF(session: SparkSession): DataFrame
}
*/
abstract class BatchSource[A <: DynamicParameters](implicit dynamicConfigType: TypeReference[A]) extends BaseObjectHasDynamicConfig[A](dynamicConfigType) with Loggable {
  /*
  override def setConfig(kvs: KeyValueParameters): Unit = {
    println(kvs)
    super.setConfig(kvs)
    if ( log.isInfoEnabled()) {
      log.info(s"source config is set to: ${this.getConfig}")
    }
  }
  */
  val context: mutable.Map[String, String] = mutable.Map[String, String]()

  def addContextEntry(key: String, value: String) = context(key) = if (value != null && !value.isEmpty) value else null

  val converts: mutable.Buffer[(DataFrame) => DataFrame] = mutable.Buffer()

  def initDf(session: SparkSession): DataFrame

  def getDF(session: SparkSession): DataFrame = {
    val initDf = this.initDf(session)
    converts.foldLeft(initDf) { (df, f) => f(df) }
  }
}
