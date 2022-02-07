package com.ebay.dataquality.controller

import com.ebay.dataquality.application.Parameters
import com.ebay.dataquality.common.TController
import com.ebay.dataquality.profiling.Loggable
import com.ebay.dataquality.service.DataProfilingService
import com.ebay.dataquality.util.EnvUtil

import java.sql.DriverManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DataProfilingController extends TController with Loggable{

  private val dataProfilingService = new DataProfilingService

  override def dispatch(option: Option[Parameters]): Unit = {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    var yesterday = LocalDate.now().minusDays(1).format(dateTimeFormatter)
    var env = "prod"
    try {
      if (option.nonEmpty){
        val parameters = option.get

        val date = parameters.date
        if (date.nonEmpty){
          dateTimeFormatter.parse(date)
          yesterday = date
        }

        env = parameters.env

        val maybeMap = EnvUtil.getEnv(env)
        if (maybeMap.isEmpty){
          System.exit(-1)
        }
        val envMap = maybeMap.get

        Class.forName(envMap("driverClass"))
        val conn = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
        val preparedStatement = conn.prepareStatement("delete from ubi_event_page where DT = ?")
        preparedStatement.setString(1, yesterday)
        preparedStatement.executeUpdate()

        // do biz
        dataProfilingService.dataAnalysis(yesterday, envMap)
      }
    } catch {
      case e: Exception =>
        log.error("Error: " + e.getMessage())
        e.printStackTrace()
    }

  }

}
