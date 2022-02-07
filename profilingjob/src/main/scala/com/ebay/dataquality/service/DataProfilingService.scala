package com.ebay.dataquality.service

import com.ebay.dataquality.common.TService
import com.ebay.dataquality.dao.DataProfilingDao
import com.ebay.dataquality.profiling.Loggable
import org.apache.spark.sql.SaveMode

class DataProfilingService extends TService with Loggable{

  private val dataProfilingDao = new DataProfilingDao

  override def dataAnalysis(yesterday: String, envMap: Map[String, String]): Any = {
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

}
