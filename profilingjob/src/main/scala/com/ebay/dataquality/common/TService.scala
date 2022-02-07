package com.ebay.dataquality.common

trait TService {

  def dataAnalysis(yesterday: String, envMap: Map[String, String]):Any

}
