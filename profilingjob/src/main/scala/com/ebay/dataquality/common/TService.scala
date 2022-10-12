package com.ebay.dataquality.common

trait TService {

  def dataAnalysis(yesterday: String, envMap: Map[String, String]): Any

  def dataAnalysis1(yesterday: String, envMap: Map[String, String]): Any

  def dataAnalysis2(yesterday: String, envMap: Map[String, String]): Any

  def dataAnalysis3(yesterday: String, envMap: Map[String, String]): Any

  def profileTagSize(yesterday: String, envMap: Map[String, String], env: String = "prod"): Any

  def profileTagSizeBot(yesterday: String, envMap: Map[String, String], env: String = "prod"): Any
}
