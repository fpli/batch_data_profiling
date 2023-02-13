package com.ebay.dataquality.pojos

import org.apache.commons.codec.digest.DigestUtils

import java.util
import java.util.{Date, StringJoiner}
import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.collection.mutable

case class InternalMetric(metricName: String, dt: String, tags: mutable.Map[String, Any], values: mutable.Map[String, Any]){

  def genMetricId(): String = {
    val sj: StringJoiner = new StringJoiner(",", metricName + "{", "}")
    if (tags.nonEmpty){
      tags.foreach {
        case (key, value) => {
          sj.add(key+ "=" + value)
        }
      }
    }
    DigestUtils.md5Hex(sj.toString)
  }

  def toIndexRequest(): util.Map[String, Object] = {
    val json: java.util.Map[String, Object] = new util.HashMap[String, Object]()
    json.put("metric_id", genMetricId())
    json.put("metric_key", metricName)
    json.put("dt", dt)
    json.put("process_time", new Date)
    if (tags.nonEmpty){
      json.put("tags", getNonTags())
    }
    if (values.nonEmpty){
      json.put("expr", getMetricValues())
    }
    json
  }



  def getNonTags(): util.Map[String, String] = {
    tags.mapValues(v => { if (v == null) "null" else v.toString}).map(identity).asJava
  }

  def getMetricValues(): util.Map[String, Any] = {
//    val metricValues: mutable.Map[String, Any] = mutable.Map()
    values.asJava
  }
}
