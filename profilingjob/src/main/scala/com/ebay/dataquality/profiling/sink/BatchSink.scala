package com.ebay.dataquality.profiling.sink

import com.ebay.dataquality.metric.ProfilerResult
import com.ebay.dataquality.{BaseObjectHasDynamicConfig, DynamicParameters, KeyValueParameters}
import com.fasterxml.jackson.core.`type`.TypeReference

abstract class BatchSink[A <: DynamicParameters](implicit dynamicConfigType: TypeReference[A]) extends BaseObjectHasDynamicConfig[A](dynamicConfigType) {
  // function to write out given ProfilerResults
  def open(kvs: KeyValueParameters): Unit = {
    this.setConfig(kvs)
  }

  def write(profilerResults: Seq[ProfilerResult])

  def close() {}
}
