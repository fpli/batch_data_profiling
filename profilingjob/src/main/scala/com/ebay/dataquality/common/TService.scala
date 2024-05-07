package com.ebay.dataquality.common

trait TService {

  def profilingPageCountNonBot(yesterday: String, envMapList: List[Map[String, String]]): Any

  def profilingPageCountBot(yesterday: String, envMapList: List[Map[String, String]]): Any

  def profileTagSize(yesterday: String, envMapList: List[Map[String, String]], env: String = "prod"): Any

  def profileTagSizeBot(yesterday: String, envMapList: List[Map[String, String]], env: String = "prod"): Any

}
