package com.ebay.dataquality.profiling

import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.Random

trait MockDataSupport {

  def peopleData(session: SparkSession): DataFrame = {
    import session.implicits._
    (1 to 100).map { i => Tuple2(randomString(5), randomInt) }.toDF("name", "age")
  }

  def getSmallItemData(session: SparkSession): DataFrame = {
    import session.implicits._

    randomItemData(0 to 3).toDF("itemid", "attr1", "attr2")
  }

  private[profiling] def randomItemData(idRange: Range): Seq[Tuple3[Int, String, String]] = {
    idRange.map(id => Tuple3(id, s"10$id", s"attr2-$id"))
  }

  private[profiling] def randomString(length: Int) = Random.alphanumeric.take(length).mkString

  private[profiling] def randomInt() = Random.nextInt(30) + 1
}
