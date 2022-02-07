package com.ebay.dataquality.profiling

import com.amazon.deequ.analyzers.runners.AnalysisRunner
import com.amazon.deequ.metrics.Metric
import com.ebay.dataquality.DateTimeUtils
import com.ebay.dataquality.config.ProfilerConfig
import com.ebay.dataquality.metric.{JDoubleMetric, ProfilerResult}
import com.ebay.dataquality.profiling.sink.SinkSupport
import com.ebay.dataquality.profiling.source.SourceSupport
import org.apache.spark.sql.SparkSession

/**
 * Run in local:
 *
 * ProfilingJobA -c ./profilingjob/samples/test_profile_config_fusion_001.json  -t "20210521 12:12:12" -l "true"
 */
object ProfilingJobA extends MetadataRegistration with ProfilerPipelineConfigSupport with SourceSupport with SinkSupport with ProfilerConfigSupport with Loggable with JobArgumentsEnabled {

  def main(args: Array[String]): Unit = {
    val optjobArguments = parse(args)
    if (optjobArguments.isEmpty) System.exit(-1)
    this.jobArguments = optjobArguments.get
    println(s"Using config file: ${jobArguments.configFileLocation}")

    SparkContext(this.jobArguments).withSparkSession {
      profilerLogicFromConfig(_)
    }
  }

  def checkConfigFusion(): Unit = {

  }

  def profilerLogicFromConfig(session: SparkSession): Unit = {
    registerMetadata

    try {
      //val jsonLocation = "json/test_profile_config_fusion_001.json"
      val jsonLocation = this.jobArguments.configFileLocation
      this.configFusion = parseConfigFromFileLocation(jsonLocation)
      checkConfigFusion()

      println(configFusion)

      val source = buildSource()
      source.addContextEntry("launchTime", this.jobArguments.launchTime)

      val dataframe = extendDataFrame(source.getDF(session))
      dataframe.select("*").show()

      import scala.collection.JavaConverters._
      val profilerConfigList = this.configFusion.getRules.get(0).getProfilers.asScala

      val profilerAndAnalyzers = profilerConfigList.map(fillInDeequAnalyzer(_))
      val analyzerResult = AnalysisRunner.onData(dataframe).addAnalyzers(profilerAndAnalyzers.map(_.deequAnalyzer)).run()

      val profilerAndMetrics = profilerAndAnalyzers.map(pa => (pa.profileConfig, analyzerResult.metric(pa.deequAnalyzer)))

      val successMetricCount = profilerAndMetrics.filter { case (_, m) => (m.get.value.isSuccess) }.length
      val failureMetricCount = profilerAndMetrics.filter { case (_, m) => (m.get.value.isFailure) }.length

      val profilerResults = profilerAndMetrics.map(pm => metricToProfilerResult(pm))
      val sink = buildSink()
      sink.write(profilerResults)
      sink.close()

    } catch {
      case e: Exception =>
        println("Error: " + e.getMessage())
        e.printStackTrace()
    }
  }

  def metricToProfilerResult(pm: (ProfilerConfig, Option[Metric[_]])) = {

    val launchTime = this.jobArguments.launchTime
    val now = DateTimeUtils.MTS_YYYYMMDDHHMMSS_1.parse(launchTime).getTime()

    val Array(startMs, endMs) =
      if (this.configFusion.getDatasources.get(0).getConfig().containsKey("dateFilter")) {
        val dateFilter = this.configFusion.getDatasources.get(0).getConfig.get("dateFilter").asInstanceOf[Map[String, Any]]
        val ago = dateFilter.get("ago").get.asInstanceOf[Int]
        val duration = dateFilter.get("duration").get.asInstanceOf[Int]
        val timeUnit = dateFilter.get("time_unit").get.asInstanceOf[String]
        DateTimeUtils.calculateStartEndDatesInMs(launchTime, "yyyyMMdd HH:mm:ss", ago, duration, timeUnit)
      } else {
        val scheduleType = this.configFusion.getRules.get(0).getConfig.get("schedule_type").asInstanceOf[String]
        if (scheduleType == null) throw new IllegalArgumentException("schedule_type is expected, especially when date_filter is absent")
        val ago = 0
        val duration = 1
        DateTimeUtils.calculateStartEndDatesInMs(launchTime, "yyyyMMdd HH:mm:ss", ago, duration, scheduleType)
      }

    val (p, m) = (pm._1, pm._2.get)
    ProfilerResult.builder()
      .configName(this.configFusion.getName)
      .name(p.getMetricName)
      .operator(p.getExpression.getOperator)
      .tag("column", m.instance)
      .tag("dataset", this.configFusion.getDatasources.get(0).getName)
      .timestamp(now)
      .from(startMs)
      .to(endMs)
      .metric(new JDoubleMetric(m.flatten()(0).value.get))
      .build()
  }
}
