package com.ebay.dataquality.profiling.sink

import com.ebay.dataquality.DynamicParameters
import com.ebay.dataquality.config.SinkConfig
import com.fasterxml.jackson.core.`type`.TypeReference

object BatchSinkFactory {

  implicit val consoleSinkConfigType: TypeReference[ConsoleSinkConfig] = new TypeReference[ConsoleSinkConfig] {}
  implicit val elasticSearchSinkConfigType: TypeReference[ElasticSearchSinkConfig] = new TypeReference[ElasticSearchSinkConfig] {}

  def createSink(config: SinkConfig): Option[BatchSink[_ <: DynamicParameters]] = {
    val optSink = config.getType match {
      case "batch.console" => Some(new ConsoleSink)
      case "batch.elasticsearch" => Some(new ElasticSearchSink)
      case _ => None
    }

    optSink.foreach(_.open(config.getConfig))
    optSink
  }
}
