package com.ebay.dataquality.service

import com.ebay.dataquality.common.TService
import com.ebay.dataquality.dao.DataProfilingDao
import com.ebay.dataquality.profiling.Loggable
import org.apache.spark.sql.SaveMode

import java.sql.DriverManager

class DataProfilingService extends TService with Loggable{

  private val dataProfilingDao = new DataProfilingDao

  override def dataAnalysis(yesterday: String, envMap: Map[String, String]): Any = {
    // clean up if necessary
    Class.forName(envMap("driverClass"))
    val conn = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
    val preparedStatement = conn.prepareStatement("delete from ubi_event_page where DT = ?")
    preparedStatement.setString(1, yesterday)
    preparedStatement.executeUpdate()

    val dataFrame = dataProfilingDao.executeSparkSQL(s"select PAGEID, count(*) total, DT from UBI_T.UBI_EVENT where DT = '${yesterday}' GROUP BY DT, PAGEID")
    // 保存数据
    dataFrame.write.mode(SaveMode.Append)
      .format("jdbc")
      .option("url", envMap("jdbcURL"))
      .option("driver", envMap("driverClass"))
      .option("user", envMap("user"))
      .option("password", envMap("password"))
      .option("dbtable", "ubi_event_page")
      .save()
  }

  override def dataAnalysis1(yesterday: String, envMap: Map[String, String]): Any = {
    // clean up if necessary
    Class.forName(envMap("driverClass"))
    val conn = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
    val preparedStatement = conn.prepareStatement("delete from ubi_event_page_bot where DT = ?")
    preparedStatement.setString(1, yesterday)
    preparedStatement.executeUpdate()

    val dataFrame = dataProfilingDao.executeSparkSQL(s"select PAGEID, count(*) total, DT from UBI_T.UBI_EVENT_SKEW where DT = '${yesterday}' GROUP BY DT, PAGEID")
    // 保存数据
    dataFrame.write.mode(SaveMode.Append)
      .format("jdbc")
      .option("url", envMap("jdbcURL"))
      .option("driver", envMap("driverClass"))
      .option("user", envMap("user"))
      .option("password", envMap("password"))
      .option("dbtable", "ubi_event_page_bot")
      .save()
  }

  override def dataAnalysis2(yesterday: String, envMap: Map[String, String]): Any = {
    // clean up if necessary
    Class.forName(envMap("driverClass"))
    val conn = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
    val preparedStatement = conn.prepareStatement("delete from profiling_page_count where DT = ?")
    preparedStatement.setString(1, yesterday)
    preparedStatement.executeUpdate()

    val dataFrame = dataProfilingDao.executeSparkSQL(s"select PAGEID page_id, count(*) total, DT from UBI_T.UBI_EVENT where DT = '${yesterday}' GROUP BY DT, PAGEID")
    // 保存数据
    dataFrame.write.mode(SaveMode.Append)
      .format("jdbc")
      .option("url", envMap("jdbcURL"))
      .option("driver", envMap("driverClass"))
      .option("user", envMap("user"))
      .option("password", envMap("password"))
      .option("dbtable", "profiling_page_count")
      .save()
  }

  override def dataAnalysis3(yesterday: String, envMap: Map[String, String]): Any = {
    // clean up if necessary
    Class.forName(envMap("driverClass"))
    val conn = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
    val preparedStatement = conn.prepareStatement("delete from profiling_page_count_bot where DT = ?")
    preparedStatement.setString(1, yesterday)
    preparedStatement.executeUpdate()

    val dataFrame = dataProfilingDao.executeSparkSQL(s"select PAGEID page_id, count(*) total, DT from UBI_T.UBI_EVENT_SKEW where DT = '${yesterday}' GROUP BY DT, PAGEID")
    // 保存数据
    dataFrame.write.mode(SaveMode.Append)
      .format("jdbc")
      .option("url", envMap("jdbcURL"))
      .option("driver", envMap("driverClass"))
      .option("user", envMap("user"))
      .option("password", envMap("password"))
      .option("dbtable", "profiling_page_count_bot")
      .save()
  }

}
