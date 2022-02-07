package com.ebay.dataquality.profiling

import com.ebay.dataquality.{DefaultObjectMapper, ProfilerPipelineConfigFusion}

import java.nio.file.Paths

trait ProfilerPipelineConfigSupport extends HasConfigFusion {

  protected def parseConfigFromURL(jsonLocation: String) = {
    val url = this.getClass.getClassLoader.getResource(jsonLocation)
    if (null == url) throw new IllegalArgumentException(s"'${jsonLocation}' is not found.")
    this.configFusion = DefaultObjectMapper.fromURL(url, classOf[ProfilerPipelineConfigFusion]);
    configFusion
  }

  protected def parseConfigFromFileLocation(fileLocation: String): ProfilerPipelineConfigFusion = {
    val url = Paths.get(fileLocation).toUri.toURL
    if (null == url) throw new IllegalArgumentException(s"'${fileLocation}' is not found.")
    this.configFusion = DefaultObjectMapper.fromURL(url, classOf[ProfilerPipelineConfigFusion]);
    configFusion
  }

}
