package com.ebay.dataquality.dao

import com.ebay.dataquality.common.TDao
import com.ebay.dataquality.pojos.InternalMetric
import com.ebay.dataquality.util.{Loggable, ProntoSink}
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.spark.sql.DataFrame
import org.elasticsearch.ElasticsearchException
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.client.indices.{CreateIndexRequest, GetIndexRequest}
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.{BoolQueryBuilder, QueryBuilders}
import org.elasticsearch.index.reindex.{BulkByScrollResponse, DeleteByQueryRequest}

import java.util.Date
import scala.collection.mutable

class DataProfilingDao extends TDao with Loggable {

  // tdq.profiling.metric.prod.2022-07-25
  val indexTemplate: String = "tdq.profiling.metric.%s.%s"

  //
  def dispatch(dataFrame: DataFrame, env: String, dt: String, metricName: String): Unit = {
    metricName match {
      case "profiling_tag_size" => sinkProfilingTagSizeMetric(dataFrame, env, dt)
      case "profiling_tag_size_bot" => sinkProfilingTagSizeMetric(dataFrame, env, dt, true)
    }
  }

  // select PAGE_ID page_id, app, event_family, tagSizeUDAF(sojMap) as tag_size_attr, dt from t1 group by dt, PAGE_ID, app, event_family
  def sinkProfilingTagSizeMetric(dataFrame: DataFrame, env: String, dt: String, bot: Boolean = false): Unit ={
    // first we use BulkRequest, we can choose BulkProcessor
    val metricName = "profiling_tag_size"
    val objectMapper = new ObjectMapper
    val index: String = indexTemplate.format(env, dt)
    val getIndexRequest = new GetIndexRequest(index)
    val restHighLevelClient: RestHighLevelClient = ProntoSink.getRestHighLevelClientInstance()
    val exist: Boolean = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT)
    if (exist) {
      val deleteByQueryRequest = new DeleteByQueryRequest(index)
      deleteByQueryRequest.setConflicts("proceed")

      val boolQueryBuilder: BoolQueryBuilder = QueryBuilders.boolQuery()
      boolQueryBuilder.must(QueryBuilders.termQuery("metric_name", metricName))
      boolQueryBuilder.must(QueryBuilders.termQuery("dt", dt))
      boolQueryBuilder.must(QueryBuilders.termQuery("tags.bot", bot))

      deleteByQueryRequest.setQuery(boolQueryBuilder)

      println(deleteByQueryRequest)
      val bulkByScrollResponse: BulkByScrollResponse = ProntoSink.getRestHighLevelClientInstance().deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT)
      println(bulkByScrollResponse)
    } else {
      val createIndexRequest = new CreateIndexRequest(index)
      createIndexRequest.settings(Settings.builder().put("index.mapping.total_fields.limit", 54000))
      val createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT)
      println(createIndexResponse)
    }

    var errorCnt = 0L
    dataFrame.collect().foreach {
      row => {
        val tags: mutable.Map[String, Any] = mutable.Map[String, Any]("bot" -> bot)
        tags.put("page_id", row.getInt(0))
        tags.put("app", row.getString(1))
        tags.put("event_family", row.getString(2))

        val dt: Date = row.getDate(4)

        val values: mutable.Map[String, Any] = mutable.Map[String, Any]()
        values.put("tag_size_attr", objectMapper.readValue(row.getString(3), classOf[java.util.Map[String, Object]]))

        val internalMetric: InternalMetric = InternalMetric(metricName, FastDateFormat.getInstance("yyyy-MM-dd").format(dt), tags, values)
        val indexRequest: IndexRequest = new IndexRequest(index).id(internalMetric.genMetricId())
        val json: java.util.Map[String, Object] = internalMetric.toIndexRequest()
        val payload = objectMapper.writeValueAsString(json)
        indexRequest.source(payload, XContentType.JSON)
        try {
          val indexResponse: IndexResponse = ProntoSink.getRestHighLevelClientInstance().index(indexRequest, RequestOptions.DEFAULT)
          println(indexResponse)
        } catch {
          case e: ElasticsearchException =>
            errorCnt += 1
            log.error(e.getMessage)
            log.error(payload)
        }
      }
    }
    println("the count of error: " + errorCnt)
  }

}
