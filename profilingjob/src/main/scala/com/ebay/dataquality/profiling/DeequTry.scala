package com.ebay.dataquality.profiling

import com.amazon.deequ.analyzers.runners.AnalysisRunner
import com.amazon.deequ.profiles.{ColumnProfilerRunner, ColumnProfiles}
import com.ebay.dataquality.metric.ProfilerResult

//import com.ebay.dss.dataquality.profiling.json.ProfileResultSerde
import org.apache.spark.sql.SparkSession

object DeequTry extends MockDataSupport with ProfilerConfigV1Support {

  def main(args: Array[String]): Unit = {

    SparkContext(JobArguments("", "Name", isLocal = true)).withSparkSession {
//      peopleProfiler(_)
      profile1(_)
    }
    //println(this.testProfilersFromJson)
  }

  def profile1(session: SparkSession): Unit = {
    //val df = this.getSmallItemData(session)
    val df = this.peopleData(session)

    val results = ColumnProfilerRunner()
      .onData(df)
      //.setPredefinedTypes(Map("attr1"->DataTypeInstances.Integral))
      .run()
    println(ColumnProfiles.toJson(results.profiles.map(_._2).toSeq))

    val profile = results.profiles("age")
    profile.histogram.foreach {
      _.values.foreach { case (key, entry) =>
        println(s"\t$key occurred ${entry.absolute} times (ratio is ${entry.ratio})")
      }
    }
  }

  def peopleProfiler(session: SparkSession): Unit = {

    import com.ebay.dataquality.profiling.ProfilerConfigV1Wrapper.toWrapper

    import scala.collection.JavaConversions._

    val peopleDF = this.peopleData(session)
    peopleDF.show(100)

    val now = System.currentTimeMillis()

    val profilerConfigs = testProfilersFromJson

    val validCA = profilerConfigs.getConfigList.toSeq.flatMap(c => c.fillAnalyzer())

    val analysisResult = AnalysisRunner.onData(peopleDF).addAnalyzers(validCA.map(_._2)).run()
    println(analysisResult)

    validCA.map(t => (t._1, analysisResult.metric(t._2)))
      //todo: handle value failure case.
      .filter(t => t._2.isDefined && t._2.get.value.isSuccess)
      //.map( t => new ProfilerResult(System.currentTimeMillis(), t._1.getName, testProfilersFromJson.getDataset, t._1.getProfiler, t._2.get ))
      .map(t => {
        val metric = t._2.get
        ProfilerResult.builder()
          .configName(profilerConfigs.getName)
          .name(t._1.getName)
          .timestamp(now)
          .operator(t._1.getProfiler)
          .tag("column", metric.instance)
          .tag("dataset", profilerConfigs.getDatasources.get(0).getName)
          //.metric(metric)
          .build()
      })
      .foreach(x => println(ProfilerResult.toJson(x)))

    validCA
      .map(t => (t._1, analysisResult.metric(t._2)))
      //todo: handle value failure case.
      .filter(t => t._2.isDefined && t._2.get.value.isFailure)
      .foreach(t => {
        try {
          t._2.get.value.get
        } catch {
          case e => e.printStackTrace()
        }
      })
    //.foreach ( x => println(ProfileResultSerde.serialize(x)) )

    /*
    val json = AnalyzerContext.successMetricsAsJson(analysisResult)
    println("json", json)
    */
    //println(peopleProfilerConfig)
  }
}
