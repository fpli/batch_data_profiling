package com.ebay.dataquality.profiling.study.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark02_RDD_File {

  def main(args: Array[String]): Unit = {
    // TODO env
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(sparkConf)

    //sc.textFile("/Users/fangpli/IdeaProjects/batch_data_profiling/profilingjob/datas/1.txt")
    val rdd = sc.textFile("datas/1.txt")

    
    rdd.collect().foreach(println)
    // TODO
    sc.stop()
  }

}
