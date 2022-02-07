package com.ebay.dataquality.profiling.source

import com.ebay.dataquality.{DynamicConfigType, DynamicParameters}
import com.fasterxml.jackson.core.`type`.TypeReference

/**
 * Steps to support a new Source type.
 * 0. register source type in MetadataRegistration
 * 1. new Config class extends DynamicParameter
 * 2. new Source class extends BatchSource
 * 3. Add implicit TypeReference statement in SourceFactory for newly created Config
 * 4. Add case statement in SourceFactory for newly created Source.
 */
object BatchSourceFactory {
  implicit val inMemoryConfig: TypeReference[InMemoryConfig] = new TypeReference[InMemoryConfig] {}
  implicit val csvConfig: TypeReference[CsvConfig] = new TypeReference[CsvConfig] {}
  implicit val tableConfig: TypeReference[TableConfig] = new TypeReference[TableConfig] {}

  def createSource(configType: DynamicConfigType): Option[BatchSource[_ <: DynamicParameters]] = {
    configType.getType match {
      case "batch.inmemory" => Some(new InMemorySource())
      case "batch.csv" => Some(new CsvSource())
      case "batch.table" => Some(new TableSource())
      case _ => Option.empty
    }
  }
}