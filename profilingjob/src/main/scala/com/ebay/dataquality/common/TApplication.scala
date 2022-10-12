package com.ebay.dataquality.common

import com.ebay.dataquality.util.EnvUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait TApplication {

  def start(master:String = "local[*]", app:String = "Application")( op : => Unit ): Unit = {
    val sparkConf = new SparkConf().setMaster(master).setAppName(app)
    val sparkSession = SparkSession.builder().enableHiveSupport().config(sparkConf).getOrCreate()
    EnvUtil.put(sparkSession)

    try {
      op
    } catch {
      case ex => println(ex.getMessage)
    }

    sparkSession.stop()
    EnvUtil.clear()
  }
}
