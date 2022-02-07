package com.ebay.dataquality.profiling.source

import com.ebay.dataquality.DynamicConfigTypeFactory
import com.ebay.dataquality.config.ProfilerConfig
import com.ebay.dataquality.profiling.{HasConfigFusion, TransformExpression}
import org.apache.spark.sql.DataFrame

import scala.collection.JavaConverters.asScalaBufferConverter

trait SourceSupport extends HasConfigFusion {

  protected def buildSource() = {
    val datasourceConfig = configFusion.getDatasources.get(0)
    val optConfigType = DynamicConfigTypeFactory.SOURCE.fromType(datasourceConfig.getType)
    val configType = optConfigType.orElse(null)
    if (configType == null) throw new IllegalArgumentException("unknown SOURCE config type: " + datasourceConfig.getType)

    val optSource = BatchSourceFactory.createSource(configType)

    optSource match {
      case Some(source) => {
        source.setConfig(datasourceConfig.getConfig)
        source
      }
      case _ => throw new IllegalArgumentException("no SOURCE found for type: " + optConfigType.get().getType)
    }
  }

  /**
   * Create and return a new dataframe from input DF and transformations.
   */
  def extendDataFrame(df: DataFrame): DataFrame = {
    val list: Seq[ProfilerConfig] = configFusion.getRules.get(0).getProfilers.asScala
    list.flatMap(f => if (f.getTransformations == null) None else f.getTransformations.asScala).foldLeft(df) {
      (newDf, tc) => newDf.withColumn(tc.getAlias, TransformExpression.toColumn(tc.getExpression))
    }
  }

}
