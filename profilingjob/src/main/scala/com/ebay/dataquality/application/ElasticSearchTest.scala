package com.ebay.dataquality.application

import com.ebay.dataquality.common.TApplication
import org.apache.http.HttpHost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.action.bulk.{BulkRequest, BulkResponse}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.client.{RequestOptions, RestClient, RestClientBuilder, RestHighLevelClient}
import org.elasticsearch.common.xcontent.XContentType

object ElasticSearchTest extends TApplication {

  def main(args: Array[String]): Unit = {
    start("yarn", "ElasticSearchTest"){
      val restClientBuilder: RestClientBuilder = RestClient.builder(new HttpHost("estdq-datalvs.vip.ebay.com", 443, "https"))
//      val restClientBuilder = RestClient.builder(HttpHost.create("https://estdq-datalvs.vip.ebay.com"))
      val basicCredentialsProvider = new BasicCredentialsProvider
      basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("76361a1177564b72bdba1ddb23d17c58", "1tIU7GQSADe4bCGGifWLfYYJSZGA8Szlc3ZWDMnd3gMhk43XPLUpw9Mv5viYQZ73"))
//      basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("76361a1177564b72bdba1ddb23d17c58", "1tIU7GQSADe4bCGGifWLfYYJSZGA8Szlc3ZWDMnd3gMhk43XPLUpw9Mv5viYQZ73"))
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

      println("*****************")
      val bulkRequest: BulkRequest = new BulkRequest

      val indexRequest = new IndexRequest()
      val map = Map("name" -> "john", "age" -> 30, "sex" -> "male")
      indexRequest.index("user").id("1002").source(map, XContentType.JSON)
      bulkRequest.add(indexRequest)
//      bulkRequest.add(new IndexRequest().index("user").id("1001").source(XContentType.JSON, "name", "allen", "age", 30, "sex", "male"))
//      bulkRequest.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON, "name", "john", "age", 30, "sex", "female"))
//      bulkRequest.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON, "name", "chandler", "age", 40, "sex", "male"))
//      bulkRequest.add(new IndexRequest().index("user").id("1004").source(XContentType.JSON, "name", "riche", "age", 40, "sex", "female"))
//      bulkRequest.add(new IndexRequest().index("user").id("1005").source(XContentType.JSON, "name", "riche1", "age", 50, "sex", "male"))
//      bulkRequest.add(new IndexRequest().index("user").id("1006").source(XContentType.JSON, "name", "riche2", "age", 50, "sex", "male"))
//      bulkRequest.add(new IndexRequest().index("user").id("1007").source(XContentType.JSON, "name", "riche33"))

      val bulkResponse: BulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT)
      println(bulkResponse.getTook)
      bulkResponse.getItems.foreach(item=>println(item))

      restHighLevelClient.close()
    }
  }
}
