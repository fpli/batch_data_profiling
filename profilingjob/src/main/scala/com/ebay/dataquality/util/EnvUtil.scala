package com.ebay.dataquality.util

import org.apache.spark.sql.SparkSession

object EnvUtil {

  private val sparkLocal = new ThreadLocal[SparkSession]

  private val envMap: Map[String, Map[String, String]] = Map(
    "qa" -> Map(
      "driverClass" -> "com.mysql.cj.jdbc.Driver",
      "jdbcURL" -> "jdbc:mysql://mysql-master-svc-qa.ido-ns.svc.57.tess.io:3306/tdq?verifyServerCertificate=false&useSSL=true&rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf8",
      "user" -> "dataops",
      "password" -> "dataops#123"
    ),
    "prod" -> Map(
      "driverClass" -> "com.mysql.cj.jdbc.Driver",
      "jdbcURL" -> "jdbc:mysql://mysqlidodb.db.stratus.ebay.com:3306/tdq?verifyServerCertificate=false&useSSL=true&rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC",
      "user" -> "uc4_user",
      "password" -> "uc4_user"
    )
  )

  private val requestMap: Map[String, String] = Map(
    "0" -> "page_nonBot",
    "1" -> "page_bot",
    "2" -> ""
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

}
