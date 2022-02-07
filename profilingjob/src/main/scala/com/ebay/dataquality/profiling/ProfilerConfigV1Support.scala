package com.ebay.dataquality.profiling

import com.ebay.dataquality.config.ConfigParamKey
import com.ebay.dataquality.{ProfilerConfigV1, ProfilerConfigsV1}

trait ProfilerConfigV1Support {

  def peopleProfilerConfigs = {
    Seq(
      ProfilerConfigV1.newInstance("MIN_OF_AGE", "Minimum",
        ConfigParamKey.WHERE, "age < 10",
        ConfigParamKey.COLUMN, "age"
      ),

      ProfilerConfigV1.newInstance("SIZE_OF_people", "Size",
        ConfigParamKey.WHERE, "age < 10"
      )
    )
  }

  private[profiling] def testProfilersFromJson = {
    val url = this.getClass.getClassLoader.getResource("json/test_profilers.json")
    println(s"url = $url")
    ProfilerConfigsV1.fromURL(url)
  }
}
