package com.ebay.dataquality.util

import org.apache.spark.sql.SparkSession


object EnvUtil {

  private val sparkLocal = new ThreadLocal[SparkSession]

  private val envMap: Map[String, List[Map[String, String]]] = Map(
    "qa" -> List(Map(
      "driverClass" -> "com.mysql.cj.jdbc.Driver",
      "jdbcURL" -> "jdbc:mysql://mysql-master-svc-qa.ido-ns.svc.57.tess.io:3306/tdq?verifyServerCertificate=false&useSSL=true&rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf8",
      "user" -> "dataops",
      "password" -> "dataops#123"
    )),
    "prod" -> List(
      Map(
      "driverClass" -> "com.mysql.cj.jdbc.Driver",
      "jdbcURL" -> "jdbc:mysql://mysqlidodb.db.stratus.ebay.com:3306/tdq?verifyServerCertificate=false&useSSL=true&rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC",
      "user" -> "uc4_user",
      "password" -> "uc4_user"),
      Map(
      "driverClass" -> "com.mysql.cj.jdbc.Driver",
      "jdbcURL" -> "jdbc:mysql://mysqltdqdb.db.stratus.ebay.com:3306/tdq?verifyServerCertificate=false&useSSL=true&rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC",
      "user" -> "tdq_app",
      "password" -> "pA_tdqap_22")
  ))

  private val requestMap: Map[String, String] = Map(
    "0" -> "profiling page count on ubi_event",
    "1" -> "profiling page count on ubi_event_skew",
    "2" -> "profiling tag size on ubi_event and ubi_event_skew",
  )

  def put(sparkSession: SparkSession): Unit = {
    sparkLocal.set(sparkSession)
  }

  def take(): SparkSession = {
    sparkLocal.get()
  }

  def clear(): Unit = {
    sparkLocal.remove()
  }

  def getEnv(env: String) = {
    envMap.get(env)
  }

  def getRequest(request: String) = {
    requestMap.get(request)
  }
}
