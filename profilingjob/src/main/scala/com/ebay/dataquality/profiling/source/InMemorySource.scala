package com.ebay.dataquality.profiling.source

import com.ebay.dataquality.DynamicParameters
import com.ebay.dataquality.profiling.MockDataSupport
import com.ebay.dataquality.profiling.source.BatchSourceFactory._
import org.apache.spark.sql.{DataFrame, SparkSession}

case class InMemoryConfig(size: Int, name: String) extends DynamicParameters {}

class InMemorySource extends BatchSource[InMemoryConfig] with MockDataSupport {

  override def initDf(session: SparkSession): DataFrame = {
    import session.implicits._
    println("config = " + this.getConfig)

    (1 to 100).map { _ => Tuple2(randomString(5), randomInt) }.toDF("name", "age")
  }

}
