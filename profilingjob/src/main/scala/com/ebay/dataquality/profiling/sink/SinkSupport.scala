package com.ebay.dataquality.profiling.sink

import com.ebay.dataquality.profiling.HasConfigFusion

trait SinkSupport extends HasConfigFusion {

  protected def buildSink() = {
    val sinkConfig = configFusion.getSinks.get(0)
    /*
    val optConfigType = DynamicConfigTypeFactory.SINK.fromType(sinkConfig.getType)
    val configType = optConfigType.orElse(null)
    if ( null == configType ) throw new IllegalArgumentException(s"Unknown SINK config type: ${sinkConfig.getType}, register it in MetadataRegistration")
    */

    val optSink = BatchSinkFactory.createSink(sinkConfig)
    optSink match {
      case Some(sink) => sink
      case _ => throw new IllegalArgumentException("no SINK found for type: " + sinkConfig.getType + ", register it in MetadataRegistration")
    }
  }

}
