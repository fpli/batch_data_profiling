package com.ebay.dataquality.profiling

import com.ebay.dataquality.DefaultObjectMapper
import com.ebay.dataquality.config.params.{DynamicConfigType, DynamicConfigTypeFactory}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

trait MetadataRegistration {

  // initialization setup
  protected def registerMetadata: Unit = {
    // scala jackson support
    DefaultObjectMapper.getObjectMapper.registerModule(DefaultScalaModule)

    // datasource metadata registration.
    DynamicConfigTypeFactory.SOURCE.register(DynamicConfigType.builder().`type`("batch.inmemory").build())
    DynamicConfigTypeFactory.SOURCE.register(DynamicConfigType.builder().`type`("batch.csv").build())
    DynamicConfigTypeFactory.SOURCE.register(DynamicConfigType.builder().`type`("batch.table").build())

    //sink registration
    DynamicConfigTypeFactory.SINK.register(DynamicConfigType.builder().`type`("batch.console").build())
    DynamicConfigTypeFactory.SINK.register(DynamicConfigType.builder().`type`("batch.elasticsearch").build())
  }
}
