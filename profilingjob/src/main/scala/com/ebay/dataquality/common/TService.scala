package com.ebay.dataquality.common

trait TService {

  def dataAnalysis(yesterday: String, envMap: Map[String, String]):Any

  def dataAnalysis1(yesterday: String, envMap: Map[String, String]):Any
}
