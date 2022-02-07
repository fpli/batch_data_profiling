package com.ebay.dataquality.profiling

import org.apache.spark.sql.SparkSession

/**
 * To mixed with Local spark test
 */
trait SparkContextSupport {
  def withSparkSession(func: SparkSession => Any): Unit = {
    val session = setupSparkSession()
    try {
      func(session)
    } finally {
      stopSparkSession(session)
    }
  }

  def setupSparkSession(): SparkSession

  private def stopSparkSession(session: SparkSession): Unit = {
    session.stop()
  }
}

case class SparkContext(jobArguments: JobArguments) extends SparkContextSupport {
  override def setupSparkSession(): SparkSession = {
    val builder = SparkSession.builder()
    if (jobArguments.isLocal) {
      builder.master("local")
        .config("spark.ui.enabled", "false")
        .config("spark.sql.shuffle.partitions", 2.toString)
    } else {
      //enable hive support for cluster mode
      builder.enableHiveSupport()
    }
    builder.appName(jobArguments.appName)
    val session = builder.getOrCreate()
    if (jobArguments.isLocal) {
      session.sparkContext.setCheckpointDir(System.getProperty("java.io.tmpdir"))
    }
    session
  }

}
