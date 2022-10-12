package com.ebay.dataquality.controller

import com.ebay.dataquality.application.Parameters
import com.ebay.dataquality.common.TController
import com.ebay.dataquality.service.DataProfilingService
import com.ebay.dataquality.util.{EnvUtil, Loggable}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DataProfilingController extends TController with Loggable{

  private val dataProfilingService = new DataProfilingService

  override def dispatch(option: Option[Parameters]): Unit = {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    var yesterday = LocalDate.now().minusDays(1).format(dateTimeFormatter)
    val negateThirdDay = LocalDate.now().minusDays(2).format(dateTimeFormatter)
    var env = "prod"
    try {
      if (option.nonEmpty){
        val parameters = option.get
        if (parameters.request == "-1"){
          System.exit(-1)
        }
        val date = parameters.date
        if (date.nonEmpty){
          yesterday = date
        }

        env = parameters.env

        val maybeMap = EnvUtil.getEnv(env)
        if (maybeMap.isEmpty){
          System.exit(-1)
        }
        val envMap = maybeMap.get

        log.info("request: "+ parameters.request)

        // do biz
        parameters.request match {
          case "0" =>
            dataProfilingService.dataAnalysis(yesterday, envMap)
          case "1" =>
            dataProfilingService.dataAnalysis1(yesterday, envMap)
          case "2" =>
            dataProfilingService.dataAnalysis2(negateThirdDay, envMap)
            dataProfilingService.dataAnalysis2(yesterday, envMap)
          case "3" =>
            dataProfilingService.dataAnalysis3(negateThirdDay, envMap)
            dataProfilingService.dataAnalysis3(yesterday, envMap)
          case "4" =>
            dataProfilingService.profileTagSize(DateTime.parse(yesterday, DateTimeFormat.forPattern("yyyyMMdd")).toString("yyyy-MM-dd"), envMap, env)
            dataProfilingService.profileTagSizeBot(DateTime.parse(yesterday, DateTimeFormat.forPattern("yyyyMMdd")).toString("yyyy-MM-dd"), envMap, env)
          case _ => System.exit(-1)
        }
      }
    } catch {
      case e: Exception =>
        log.error("Error: " + e.getMessage)
        e.printStackTrace()
    }

  }

}
