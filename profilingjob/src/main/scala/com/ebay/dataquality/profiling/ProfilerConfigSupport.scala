package com.ebay.dataquality.profiling

import com.amazon.deequ.analyzers._
import com.amazon.deequ.metrics.Metric
import com.ebay.dataquality.config.ProfilerConfig
import com.ebay.dataquality.config.params.profiler._
import com.fasterxml.jackson.core.`type`.TypeReference

trait ProfilerConfigSupport extends HasConfigFusion {

  def fillInDeequAnalyzer(profilerConfig: ProfilerConfig) = {
    val expr = profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if (null == filter || filter.trim.isEmpty) Option.empty else Some(filter)

    val analyzer = expr.getOperator match {
      case "Size" => Size(optFilter)
      case "Maximum" => {
        val params = expr.getConfig.toSpecificType(new TypeReference[MaximumParameters]() {})
        Maximum(params.getColumn, optFilter)
      }
      case "Minimum" => {
        val params = expr.getConfig.toSpecificType(new TypeReference[MinimumParameters]() {})
        Minimum(params.getColumn, optFilter)
      }
      case "Mean" => {
        val params = expr.getConfig.toSpecificType(new TypeReference[MeanParameters]() {})
        Mean(params.getColumn, optFilter)
      }
      case "MaxLength" => {
        val params = expr.getConfig.toSpecificType(new TypeReference[MaxLengthParameters]() {})
        MaxLength(params.getColumn, optFilter)
      }
      case "MinLength" => {
        val params = expr.getConfig.toSpecificType(new TypeReference[MinLengthParameters]() {})
        MinLength(params.getColumn, optFilter)
      }
      case "ApproxCountDistinct" => {
        val params = expr.getConfig.toSpecificType(new TypeReference[ApproxCountDistinctParameters]() {})
        ApproxCountDistinct(params.getColumn, optFilter)
      }
      case "Completeness" => {
        val params = expr.getConfig.toSpecificType(new TypeReference[CompletenessParameters]() {})
        Completeness(params.getColumn, optFilter)
      }
      case "ApproxQuantile" => {
        val params = expr.getConfig.toSpecificType(new TypeReference[ApproxQuantileParameters]() {})
        ApproxQuantile(params.getColumn, params.getQuantile, params.getRelativeError, optFilter)
      }
      case "Sum" => {
        val params = expr.getConfig.toSpecificType(new TypeReference[SumParameters]() {})
        Sum(params.getColumn, optFilter)
      }
      case "Uniqueness" => {
        val params = expr.getConfig.toSpecificType(new TypeReference[UniquenessParameters]() {})
        Uniqueness(params.getColumn, optFilter)
      }
      case operator: String => throw new IllegalArgumentException(s"unknown profiler operation $operator")
    }

    ProfilerConfigWithDeequAnalyzer(profilerConfig, analyzer)
  }

}

case class ProfilerConfigWithDeequAnalyzer(profileConfig: ProfilerConfig, deequAnalyzer: Analyzer[_ <: State[_], _ <: Metric[_]])
