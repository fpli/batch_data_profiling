package com.ebay.dataquality.common

trait TService {

  def dataAnalysis(yesterday: String, envMapList: List[Map[String, String]]): Any

  def dataAnalysis1(yesterday: String, envMapList: List[Map[String, String]]): Any

  def dataAnalysis2(yesterday: String, envMapList: List[Map[String, String]]): Any

  def dataAnalysis3(yesterday: String, envMapList: List[Map[String, String]]): Any

  def profileTagSize(yesterday: String, envMapList: List[Map[String, String]], env: String = "prod"): Any

  def profileTagSizeBot(yesterday: String, envMapList: List[Map[String, String]], env: String = "prod"): Any

  def collectPageTagMapping(yesterday: String, env: String = "prod"): Unit
  def collectPageTagMappingBot(yesterday: String, env: String = "prod"): Unit

  def collectPageModuleMapping(yesterday: String, env: String = "prod"): Unit

  def collectPageClickMapping(yesterday: String, env: String = "prod"): Unit

  def collectBotConsistencyGap(yesterday: String, envMap: List[Map[String, String]]): Any

}
