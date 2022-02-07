package com.ebay.dataquality.profiling.sink

import com.ebay.dataquality.DynamicParameters
import com.ebay.dataquality.metric.ProfilerResult
import com.ebay.dataquality.profiling.sink.BatchSinkFactory.consoleSinkConfigType
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @param rowLimit   -- number of rows to print out to console
 * @param jsonFormat -- output json format or not
 * @param stderr     -- print to stderr or stdout
 */
case class ConsoleSinkConfig(
                              @JsonProperty("row-limit") rowLimit: Int = 1000,
                              @JsonProperty("json-format") jsonFormat: Boolean = true,
                              stderr: Boolean = false
                            ) extends DynamicParameters

class ConsoleSink extends BatchSink[ConsoleSinkConfig] {

  override def write(profilerResults: Seq[ProfilerResult]): Unit = {
    val limitResults = profilerResults.take(this.getConfig.rowLimit)
    val out = if (this.getConfig.stderr) System.err else System.out

    if (this.getConfig.jsonFormat) {
      limitResults.map(ProfilerResult.toJson(_)).foreach(out.println)
    } else {
      limitResults.foreach(out.println)
    }
  }

}
