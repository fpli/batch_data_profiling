package com.ebay.dataquality.profiling

import com.amazon.deequ.profiles.{ColumnProfilerRunner, ColumnProfiles}
import com.ebay.dataquality.DateTimeUtils
import com.ebay.dataquality.metric.{JKeyedDoubleMetric, ProfilerResult}
import com.ebay.dataquality.profiling.ProfilingJobA.checkConfigFusion
import com.ebay.dataquality.profiling.sink.SinkSupport
import com.ebay.dataquality.profiling.source.SourceSupport
import com.ebay.dataquality.util.Loggable
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

import java.time.LocalDate

object ProfilingJob extends MetadataRegistration with ProfilerPipelineConfigSupport with SourceSupport with SinkSupport with ProfilerConfigSupport with Loggable with JobArgumentsEnabled {

  def main(args: Array[String]): Unit = {
    val optjobArguments = parse(args)
    if (optjobArguments.isEmpty) System.exit(-1)
    this.jobArguments = optjobArguments.get
    println(s"Using config file: ${jobArguments.configFileLocation}")

    SparkContext(this.jobArguments).withSparkSession {
      profile(_)
    }
  }

  def profile(spark: SparkSession): Unit = {
    registerMetadata

    try {
      val jsonLocation = this.jobArguments.configFileLocation
      this.configFusion = parseConfigFromFileLocation(jsonLocation)
      checkConfigFusion()
      println(configFusion)

      val dataFrame = spark.sql("select PAGEID, count(*) total, DT from UBI_T.UBI_EVENT where DT = '20220205' GROUP BY DT, PAGEID")

      dataFrame.printSchema()
      // 保存数据
      dataFrame.write
        .format("jdbc")
        .option("url", "jdbc:mysql://mysql-master-svc-qa.ido-ns.svc.57.tess.io:3306/tdq?verifyServerCertificate=false&useSSL=true&rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf8")
        .option("driver", "com.mysql.cj.jdbc.Driver")
        .option("user", "dataops")
        .option("password", "dataops#123")
        .option("dbtable", "ubi_event_page")
        .mode(SaveMode.Append)
        .save()


//      val dataFrame1 = spark.read.table("ubi_t.ubi_event")
//
//      //val source = buildSource()
//      //source.addContextEntry("launchTime", this.jobArguments.launchTime)
//
//      val dataFrame = dataFrame1.filter("DT = '20220122'")
      //println(dataFrame.count())

      //val dataframe = extendDataFrame(dataFrame)


//      println(ColumnProfiles.toJson(Seq(pageIdProfile)))
//
//      val value = new java.util.HashMap[String, java.lang.Double]
//
//
//      val jKeyedDoubleMetric = new JKeyedDoubleMetric(value)
//      pageIdProfile.histogram.foreach {
//        _.values.foreach { case (key, entry) =>
//          println(s"\t$key occurred ${entry.absolute} times (ratio is ${entry.ratio})")
//          value.put(key, entry.absolute)
//        }
//      }
//
//      println(jKeyedDoubleMetric)
//
//      val launchTime = this.jobArguments.launchTime
//      val now = DateTimeUtils.MTS_YYYYMMDDHHMMSS_1.parse(launchTime).getTime()
//
//      val Array(startMs, endMs) =
//        if (this.configFusion.getDatasources.get(0).getConfig().containsKey("dateFilter")) {
//          val dateFilter = this.configFusion.getDatasources.get(0).getConfig.get("dateFilter").asInstanceOf[Map[String, Any]]
//          val ago = dateFilter.get("ago").get.asInstanceOf[Int]
//          val duration = dateFilter.get("duration").get.asInstanceOf[Int]
//          val timeUnit = dateFilter.get("time_unit").get.asInstanceOf[String]
//          DateTimeUtils.calculateStartEndDatesInMs(launchTime, "yyyyMMdd HH:mm:ss", ago, duration, timeUnit)
//        } else {
//          val scheduleType = this.configFusion.getRules.get(0).getConfig.get("schedule_type").asInstanceOf[String]
//          if (scheduleType == null) throw new IllegalArgumentException("schedule_type is expected, especially when date_filter is absent")
//          val ago = 0
//          val duration = 1
//          DateTimeUtils.calculateStartEndDatesInMs(launchTime, "yyyyMMdd HH:mm:ss", ago, duration, scheduleType)
//        }
//
//      val result1 = ProfilerResult.builder()
//        .configName(this.configFusion.getName)
//        .name("pageidGroup")
//        .operator("group")
//        .timestamp(now)
//        .from(startMs)
//        .to(endMs)
//        .metric(jKeyedDoubleMetric)
//        .build()
//
//      val sink = buildSink()
//      sink.write(Seq(result1))
//      sink.close()

    } catch {
      case e: Exception =>
        log.error("Error: " + e.getMessage)
    }
  }
}
