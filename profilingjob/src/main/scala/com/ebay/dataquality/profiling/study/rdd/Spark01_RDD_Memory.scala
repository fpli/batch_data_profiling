package com.ebay.dataquality.profiling.study.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Memory {

  def main(args: Array[String]): Unit = {
    // TODO env
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(sparkConf)

    // TODO
    val seq = Seq[Int](1, 2, 3, 4)
    val rdd: RDD[Int] = sc.parallelize(seq)

    // TODO 
    rdd.collect().foreach(println)

    // TODO
    sc.stop()
  }

}
