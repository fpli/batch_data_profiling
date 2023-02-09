package com.ebay.dataquality.service

import com.ebay.dataquality.common.TService
import com.ebay.dataquality.dao.DataProfilingDao
import com.ebay.dataquality.pojos.ClassNameAllowedValues
import org.apache.spark.sql.{DataFrame, SaveMode}

import java.sql.{Connection, DriverManager, PreparedStatement}
import scala.collection.mutable.ArrayBuffer

class DataProfilingService extends TService {

  private val dataProfilingDao = new DataProfilingDao

  override def dataAnalysis(yesterday: String, envMapList: List[Map[String, String]]): Any = {
    envMapList.foreach(envMap => {
      // clean up if necessary
      Class.forName(envMap("driverClass"))
      val conn: Connection = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
      val preparedStatement: PreparedStatement = conn.prepareStatement("delete from ubi_event_page where DT = ?")
      preparedStatement.setString(1, yesterday)
      preparedStatement.executeUpdate()
    })

    val dataFrame: DataFrame = dataProfilingDao.executeSparkSQL(s"select PAGEID, count(*) total, DT from UBI_T.UBI_EVENT where DT = '${yesterday}' GROUP BY DT, PAGEID")

    envMapList.foreach(envMap => {
      // 保存数据
      dataFrame.write.mode(SaveMode.Append)
        .format("jdbc")
        .option("url", envMap("jdbcURL"))
        .option("driver", envMap("driverClass"))
        .option("user", envMap("user"))
        .option("password", envMap("password"))
        .option("dbtable", "ubi_event_page")
        .save()
    })
  }

  override def dataAnalysis1(yesterday: String, envMapList: List[Map[String, String]]): Any = {
    envMapList.foreach(envMap => {
      // clean up if necessary
      Class.forName(envMap("driverClass"))
      val conn: Connection = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
      val preparedStatement: PreparedStatement = conn.prepareStatement("delete from ubi_event_page_bot where DT = ?")
      preparedStatement.setString(1, yesterday)
      preparedStatement.executeUpdate()
    })

    val dataFrame: DataFrame = dataProfilingDao.executeSparkSQL(s"select PAGEID, count(*) total, DT from UBI_T.UBI_EVENT_SKEW where DT = '${yesterday}' GROUP BY DT, PAGEID")
    envMapList.foreach(envMap => {
      // 保存数据
      dataFrame.write.mode(SaveMode.Append)
        .format("jdbc")
        .option("url", envMap("jdbcURL"))
        .option("driver", envMap("driverClass"))
        .option("user", envMap("user"))
        .option("password", envMap("password"))
        .option("dbtable", "ubi_event_page_bot")
        .save()
    })
  }

  override def dataAnalysis2(yesterday: String, envMapList: List[Map[String, String]]): Any = {
    envMapList.foreach(envMap => {
      // clean up if necessary
      Class.forName(envMap("driverClass"))
      val conn: Connection = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
      val preparedStatement: PreparedStatement = conn.prepareStatement("delete from profiling_page_count where DT = ?")
      preparedStatement.setString(1, yesterday)
      preparedStatement.executeUpdate()
    })

    val dataFrame: DataFrame = dataProfilingDao.executeSparkSQL(s"select PAGEID page_id, count(*) total, DT from UBI_T.UBI_EVENT where DT = '${yesterday}' GROUP BY DT, PAGEID")
    envMapList.foreach(envMap => {
      // 保存数据
      dataFrame.write.mode(SaveMode.Append)
        .format("jdbc")
        .option("url", envMap("jdbcURL"))
        .option("driver", envMap("driverClass"))
        .option("user", envMap("user"))
        .option("password", envMap("password"))
        .option("dbtable", "profiling_page_count")
        .save()
    })
  }

  override def dataAnalysis3(yesterday: String, envMapList: List[Map[String, String]]): Any = {
    envMapList.foreach(envMap => {
      // clean up if necessary
      Class.forName(envMap("driverClass"))
      val conn: Connection = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
      val preparedStatement: PreparedStatement = conn.prepareStatement("delete from profiling_page_count_bot where DT = ?")
      preparedStatement.setString(1, yesterday)
      preparedStatement.executeUpdate()
    })

    val dataFrame: DataFrame = dataProfilingDao.executeSparkSQL(s"select PAGEID page_id, count(*) total, DT from UBI_T.UBI_EVENT_SKEW where DT = '${yesterday}' GROUP BY DT, PAGEID")
    envMapList.foreach(envMap => {
      // 保存数据
      dataFrame.write.mode(SaveMode.Append)
        .format("jdbc")
        .option("url", envMap("jdbcURL"))
        .option("driver", envMap("driverClass"))
        .option("user", envMap("user"))
        .option("password", envMap("password"))
        .option("dbtable", "profiling_page_count_bot")
        .save()
    })
  }

  override def profileTagSize(yesterday: String, envMapList: List[Map[String, String]], env: String = "prod"): Any = {
//    envMapList.foreach(envMap => {
//      Class.forName(envMap("driverClass"))
//      val conn: Connection = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
//      val preparedStatement: PreparedStatement = conn.prepareStatement("delete from profiling_tag_size_attr where dt = ?")
//      preparedStatement.setString(1, yesterday)
//      preparedStatement.executeUpdate()
//    })

    val prptyTagMap: Map[String, (String, String)] = dataProfilingDao.prptyTagMap
    val classNameToAllowedValues: Map[String, ClassNameAllowedValues] = allowedValuesByClassName(prptyTagMap)
    val specialTagAllowedValues = dataProfilingDao.specialTagValues()
    val dataFrame: DataFrame = dataProfilingDao.profileTagSize(yesterday, classNameToAllowedValues, specialTagAllowedValues)
//    envMapList.foreach(envMap => {
//      if ("qa".equals(env)){
//        dataFrame.cache()
//        dataFrame.write.mode(SaveMode.Append)
//          .format("jdbc")
//          .option("url", envMap("jdbcURL"))
//          .option("driver", envMap("driverClass"))
//          .option("user", envMap("user"))
//          .option("password", envMap("password"))
//          .option("dbtable", "profiling_tag_size_attr")
//          .save()
//      }
//    })

    dataProfilingDao.dispatch(dataFrame, env, yesterday,"profiling_tag_size")
  }

  private def allowedValuesByClassName(prptyTagMap: Map[String, (String, String)]): Map[String, ClassNameAllowedValues] = {
    val classNameToAllowedValues: Map[String, ClassNameAllowedValues] = prptyTagMap.mapValues(tuple2 => {
      val className: String = tuple2._1
      val allowedValues: String = tuple2._2
      val strings: Array[String] = allowedValues.split(",")
      val ans = new ArrayBuffer[Any](strings.length)
      className match {
        case "java.lang.String" =>
          ans.appendAll(strings)
        case "java.lang.Long" =>
          strings.foreach(s => ans.append(s.toLong))
        case "java.lang.Integer" =>
          strings.foreach(s => ans.append(s.toInt))
        case "java.lang.Double" =>
          strings.foreach(s => ans.append(s.toDouble))
        case "java.lang.Boolean" =>
          strings.foreach(s => ans.append(s.toLowerCase.toBoolean))
        case "java.lang.Float" =>
          strings.foreach(s => ans.append(s.toFloat))
        case _ => ans
      }
      ClassNameAllowedValues(className, ans)
    }).map(identity)
    classNameToAllowedValues
  }

  override def profileTagSizeBot(yesterday: String, envMapList: List[Map[String, String]], env: String = "prod"): Any = {
//    envMapList.foreach(envMap => {
//      Class.forName(envMap("driverClass"))
//      val conn: Connection = DriverManager.getConnection(envMap("jdbcURL"), envMap("user"), envMap("password"))
//      val preparedStatement: PreparedStatement = conn.prepareStatement("delete from profiling_tag_size_attr_bot where dt = ?")
//      preparedStatement.setString(1, yesterday)
//      preparedStatement.executeUpdate()
//    })

    val prptyTagMap: Map[String, (String, String)] = dataProfilingDao.prptyTagMap
    val classNameToAllowedValues: Map[String, ClassNameAllowedValues] = allowedValuesByClassName(prptyTagMap)
    val specialTagAllowedValues = dataProfilingDao.specialTagValues()
    val dataFrame: DataFrame = dataProfilingDao.profileTagSizeBot(yesterday, classNameToAllowedValues, specialTagAllowedValues)
//    envMapList.foreach(envMap => {
//      if ("qa".equals(env)){
//        dataFrame.cache()
//        dataFrame.write.mode(SaveMode.Append)
//          .format("jdbc")
//          .option("url", envMap("jdbcURL"))
//          .option("driver", envMap("driverClass"))
//          .option("user", envMap("user"))
//          .option("password", envMap("password"))
//          .option("dbtable", "profiling_tag_size_attr_bot")
//          .save()
//      }
//    })

    dataProfilingDao.dispatch(dataFrame, env, yesterday, "profiling_tag_size_bot")
  }

  override def collectPageTagMapping(yesterday: String, env: String = "prod"): Unit = {
    dataProfilingDao.collectPageTagMapping(yesterday, env)
  }

  override def collectPageTagMappingBot(yesterday: String, env: String = "prod"): Unit = {
    dataProfilingDao.collectPageTagMappingBot(yesterday, env)
  }
}
