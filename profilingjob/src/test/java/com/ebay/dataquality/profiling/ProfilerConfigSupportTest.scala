package com.ebay.dataquality.profiling

import com.amazon.deequ.analyzers._
import com.ebay.dataquality.config.params.profiler.{ApproxCountDistinctParameters, ApproxQuantileParameters, CompletenessParameters, MaxLengthParameters, MaximumParameters, MeanParameters, MinLengthParameters, MinimumParameters}
import com.ebay.dataquality.config.{ExpressionConfig, ProfilerConfig}
import com.ebay.dataquality.util.SpecialTagCheck.{checkGuid, isTableOrNot, mobileVersionCheck, paginationCheck}
import com.fasterxml.jackson.core.`type`.TypeReference
import org.apache.http.HttpHost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.client.{RequestOptions, RestClient, RestHighLevelClient}

class ProfilerConfigSupportTest extends org.scalatest.FunSuite {
  test("SizeWithOption") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("failure_metric")
      .filter("age > 0")
      .expression(
        ExpressionConfig.builder()
          .operator("Size")
          .build()
      ).build()

    val expr= profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if ( null == filter || filter.trim.isEmpty ) Option.empty else Some(filter)

    val expected = Size(Some("age > 0"))

    assertResult(expected) {
      Size(optFilter)
    }
  }

  test("MaximumWithoutOption") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("max_age")
      .filter(null)
      .expression(
        ExpressionConfig.builder()
          .operator("Maximum")
          .config(MaximumParameters.builder().column("age").build().toKV()).build()
      ).build()

    val expr= profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if ( null == filter || filter.trim.isEmpty ) Option.empty else Some(filter)
    val params = expr.getConfig.toSpecificType(new TypeReference[MaximumParameters](){})

    val expected = Maximum("age", Option.empty)

    assertResult(expected) {
      Maximum(params.getColumn, optFilter)
    }
  }

  test("MaximumWithOption") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("max_age")
      .filter("age_plus_100 > 0")
      .expression(
        ExpressionConfig.builder()
          .operator("Maximum")
          .config(MaximumParameters.builder().column("age").build().toKV()).build()
      ).build()

    val expr= profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if ( null == filter || filter.trim.isEmpty ) Option.empty else Some(filter)
    val params = expr.getConfig.toSpecificType(new TypeReference[MaximumParameters](){})

    val expected = Maximum("age", Some("age_plus_100 > 0"))

    assertResult(expected) {
      Maximum(params.getColumn, optFilter)
    }
  }

  test("MinimumWithoutOption") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("min_age")
      .filter(null)
      .expression(
        ExpressionConfig.builder()
          .operator("Minimum")
          .config(MinimumParameters.builder().column("age").build().toKV()).build()
      ).build()

    val expr= profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if ( null == filter || filter.trim.isEmpty ) Option.empty else Some(filter)
    val params = expr.getConfig.toSpecificType(new TypeReference[MinimumParameters](){})

    val expected = Minimum("age", Option.empty)

    assertResult(expected) {
      Minimum(params.getColumn, optFilter)
    }
  }

  test("MeanWithoutOption") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("mean_age")
      .filter(null)
      .expression(
        ExpressionConfig.builder()
          .operator("Mean")
          .config(MeanParameters.builder().column("age").build().toKV()).build()
      ).build()

    val expr= profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if ( null == filter || filter.trim.isEmpty ) Option.empty else Some(filter)
    val params = expr.getConfig.toSpecificType(new TypeReference[MeanParameters](){})

    val expected = Mean("age", Option.empty)

    assertResult(expected) {
      Mean(params.getColumn, optFilter)
    }
  }

  test("MaxLengthWithoutOption") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("max_length_name")
      .filter(null)
      .expression(
        ExpressionConfig.builder()
          .operator("MaxLength")
          .config(MaxLengthParameters.builder().column("name").build().toKV()).build()
      ).build()

    val expr= profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if ( null == filter || filter.trim.isEmpty ) Option.empty else Some(filter)
    val params = expr.getConfig.toSpecificType(new TypeReference[MaxLengthParameters](){})

    val expected = MaxLength("name", Option.empty)

    assertResult(expected) {
      MaxLength(params.getColumn, optFilter)
    }
  }

  test("MinLengthWithoutOption") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("min_length_name")
      .filter(null)
      .expression(
        ExpressionConfig.builder()
          .operator("MinLength")
          .config(MinLengthParameters.builder().column("name").build().toKV()).build()
      ).build()

    val expr= profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if ( null == filter || filter.trim.isEmpty ) Option.empty else Some(filter)
    val params = expr.getConfig.toSpecificType(new TypeReference[MinLengthParameters](){})

    val expected = MinLength("name", Option.empty)

    assertResult(expected) {
      MinLength(params.getColumn, optFilter)
    }
  }

  test("ApproxCountDistinctWithoutOption") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("approx_count_distinct_name")
      .filter(null)
      .expression(
        ExpressionConfig.builder()
          .operator("ApproxCountDistinct")
          .config(ApproxCountDistinctParameters.builder().column("name").build().toKV()).build()
      ).build()

    val expr= profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if ( null == filter || filter.trim.isEmpty ) Option.empty else Some(filter)
    val params = expr.getConfig.toSpecificType(new TypeReference[ApproxCountDistinctParameters](){})

    val expected = ApproxCountDistinct("name", Option.empty)

    assertResult(expected) {
      ApproxCountDistinct(params.getColumn, optFilter)
    }
  }

  test("CompletenessWithoutOption") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("completeness_age")
      .filter(null)
      .expression(
        ExpressionConfig.builder()
          .operator("Completeness")
          .config(CompletenessParameters.builder().column("age").build().toKV()).build()
      ).build()

    val expr= profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if ( null == filter || filter.trim.isEmpty ) Option.empty else Some(filter)
    val params = expr.getConfig.toSpecificType(new TypeReference[CompletenessParameters](){})

    val expected = Completeness("age", Option.empty)

    assertResult(expected) {
      Completeness(params.getColumn, optFilter)
    }
  }

  test("ApproxQuantileWithoutOption") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("approx_quantile_age")
      .filter(null)
      .expression(
        ExpressionConfig.builder()
          .operator("ApproxQuantile")
          .config(
            ApproxQuantileParameters.builder()
              .column("age")
              .quantile(0.5)
              .relativeError(0.0)
              .build().toKV()
          ).build()
      ).build()

    val expr= profilerConfig.getExpression
    val filter = profilerConfig.getFilter
    val optFilter = if ( null == filter || filter.trim.isEmpty ) Option.empty else Some(filter)
    val params = expr.getConfig.toSpecificType(new TypeReference[ApproxQuantileParameters](){})

    val expected = ApproxQuantile("age", 0.5, 0.0, Option.empty)

    assertResult(expected) {
      ApproxQuantile(params.getColumn, params.getQuantile, params.getRelativeError, optFilter)
    }
  }

  test("InvalidOperator") {
    val profilerConfig = ProfilerConfig.builder()
      .metricName("invalid_operator")
      .filter(null)
      .expression(
        ExpressionConfig.builder()
          .operator("InvalidOp")
          .config(MaximumParameters.builder().column("age").build().toKV()).build()
      ).build()

    val op = profilerConfig.getExpression.getOperator
    val supportedOp = List(
      "Size",
      "Maximum",
      "Minimum",
      "Mean",
      "MaxLength",
      "MinLength",
      "ApproxCountDistinct",
      "Completeness",
      "ApproxQuantile"
    )

    assertThrows[IllegalArgumentException] {
      if (!supportedOp.contains(op))
        throw new IllegalArgumentException(s"unknown profiler operation $op")
    }
  }

  test("elastic"){
    val restClientBuilder = RestClient.builder(HttpHost.create("estdq-datalvs.vip.ebay.com"))
    val basicCredentialsProvider = new BasicCredentialsProvider
    basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("76361a1177564b72bdba1ddb23d17c58", "1tIU7GQSADe4bCGGifWLfYYJSZGA8Szlc3ZWDMnd3gMhk43XPLUpw9Mv5viYQZ73"))
    restClientBuilder.setHttpClientConfigCallback((httpAsyncClientBuilder: HttpAsyncClientBuilder)=>{
      httpAsyncClientBuilder.setDefaultCredentialsProvider(basicCredentialsProvider)
    })
    val restHighLevelClient = new RestHighLevelClient(restClientBuilder)
    println(restHighLevelClient)
    val getIndexRequest = new GetIndexRequest("tdq.prod.metric.normal.2022-04-22")
    val getIndexResponse = restHighLevelClient.indices().get(getIndexRequest, RequestOptions.DEFAULT)
    println(getIndexResponse.getAliases)
    println(getIndexResponse.getMappings)
    println(getIndexResponse.getSettings)

    restHighLevelClient.close()
  }

  test("guid"){
    println(checkGuid("4c91c6cd17d0ad33375d2f29ffe63df5"))
    println(checkGuid("4c91c6cd17d0ad33375d2f29ffe63df5j"))
    println(checkGuid("88ca672117e0a7b9dd01a407ffeac177"))
  }

  test("mobileVersion"){
    println(mobileVersionCheck("12.12.12"))
    println(mobileVersionCheck("12x.12.12"))
  }

  test("icpp"){
    println(paginationCheck("34"))
    println(paginationCheck("34c"))
  }

  test("isTableOrNot"){
    println(isTableOrNot(null))
    println(isTableOrNot("12"))
    println(isTableOrNot(""))
    println(isTableOrNot("1"))
    println(isTableOrNot("0"))
  }
}
