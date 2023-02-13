package com.ebay.dataquality.profiling.sink

import com.ebay.dataquality.metric.{ElasticSearchProfilerResultRepository, ProfilerResult}
import com.ebay.dataquality.profiling.sink.BatchSinkFactory.elasticSearchSinkConfigType
import com.ebay.dataquality.util.Loggable
import com.ebay.dataquality.{DynamicParameters, KeyValueParameters}
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.http.HttpHost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.{RestClient, RestHighLevelClient}

import scala.collection.JavaConverters._

case class ElasticSearchSinkConfig(endpoint: String, username: String, password: String, @JsonProperty("index-prefix") indexPrefix: String = "profiling-") extends DynamicParameters

class ElasticSearchSink extends BatchSink[ElasticSearchSinkConfig] with Loggable {

  private lazy val client: RestHighLevelClient = initClient

  private lazy val repo: ElasticSearchProfilerResultRepository = initRepo

  private def initClient = {
    val strUrl = this.getConfig.endpoint
    val restClientBuilder = RestClient.builder(HttpHost.create(strUrl))

    if (this.getConfig.username != null && this.getConfig.password != null) {
      val credentialsProvider = new BasicCredentialsProvider
      credentialsProvider.setCredentials(AuthScope.ANY,
        new UsernamePasswordCredentials(this.getConfig.username, this.getConfig.password))

      restClientBuilder.setHttpClientConfigCallback((httpAsyncClientBuilder: HttpAsyncClientBuilder) => {
        httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
      })
    }

    new RestHighLevelClient(restClientBuilder)
  }

  private def initRepo = {
    ElasticSearchProfilerResultRepository.builder.client(client).indexTemplatePrefix(this.getConfig.indexPrefix).build
  }

  override def open(kvs: KeyValueParameters) {
    super.open(kvs)
    repo
  }

  override def close(): Unit = {
    client.close()
  }

  override def write(profilerResults: Seq[ProfilerResult]): Unit = {
    if (log.isInfoEnabled()) {
      log.info(s"write profiler results[$profilerResults] to [endpoint=${this.getConfig.endpoint}, index-prefix='${this.getConfig.indexPrefix}']")
    }
    repo.save(profilerResults.asJava)
  }
}
