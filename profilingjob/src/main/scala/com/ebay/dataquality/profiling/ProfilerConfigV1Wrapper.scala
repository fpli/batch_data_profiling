package com.ebay.dataquality.profiling

import com.amazon.deequ.analyzers._
import com.amazon.deequ.metrics.Metric
import com.ebay.dataquality.ProfilerConfigV1
import com.ebay.dataquality.config.{ConfigParam, ConfigParamKey, ConfigParamValue, ConfigParams}

import java.util.Optional
import scala.language.implicitConversions

object ProfilerConfigV1Wrapper {

  implicit def fromJavaOptional[T](opt: Optional[T]): Option[T] = if (opt.isPresent) Some(opt.get()) else Option.empty

  implicit def fromKeyValue[T](kv: Tuple2[String, T]) = ConfigParam.builder.key(kv._1).value(ConfigParamValue.fromValue(kv._2)).build

  implicit def fromParams(params: ConfigParam*): ConfigParams = params.foldLeft(ConfigParams.builder) {
    _.param(_)
  }.build()

  implicit def toStringType(v: ConfigParamValue): String = v.getString

  implicit def toDoubleType(v: ConfigParamValue): Double = v.getDouble

  implicit def toWrapper(config: ProfilerConfigV1): ProfilerConfigV1Wrapper = new ProfilerConfigV1Wrapper(config)

}

class ProfilerConfigV1Wrapper(val config: ProfilerConfigV1) {

  import ProfilerConfigV1Wrapper._

  def fillAnalyzer(): Option[(ProfilerConfigV1, Analyzer[_, Metric[_]])] = {

    implicit val implicitConfig = this.config

    this.config.getProfiler match {
      case "Minimum" => Some(config,
        Minimum(
          value(ConfigParamKey.COLUMN),
          optionValue(ConfigParamKey.WHERE))
      )

      case "Size" => Some(config,
        Size(optionValue(ConfigParamKey.WHERE)))

      case "Maximum" => Some(config,
        Maximum(
          value(ConfigParamKey.COLUMN),
          optionValue(ConfigParamKey.WHERE)))

      case "MaxLength" => Some(config,
        MaxLength(
          value(ConfigParamKey.COLUMN),
          optionValue(ConfigParamKey.WHERE)))

      case "MinLength" => Some(config,
        MinLength(
          value(ConfigParamKey.COLUMN),
          optionValue(ConfigParamKey.WHERE)))

      case "Mean" => Some(config,
        Mean(
          value(ConfigParamKey.COLUMN),
          optionValue(ConfigParamKey.WHERE)))

      case "ApproxQuantiles" => Some(config,
        ApproxQuantiles(
          value(ConfigParamKey.COLUMN),
          value[String](ConfigParamKey.QUANTILES).split(",").map(_.toDouble),
          optionValue[Double](ConfigParamKey.RELATIVE_ERROR).getOrElse(0.01d)
        ))
      case _ => None
    }
  }

  def optionValue[T](key: String)(implicit toType: (ConfigParamValue) => T): Option[T] = {
    val option: Option[ConfigParam] = this.config.getParamByKey(key)
    option.flatMap(_.getValue).map(toType(_))
  }

  def value[T](key: String)(implicit toType: (ConfigParamValue) => T): T = toType(config.byKeyOrThrow(key).getOrThrow())
}