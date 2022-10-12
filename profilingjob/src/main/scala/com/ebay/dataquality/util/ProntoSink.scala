package com.ebay.dataquality.util

import org.apache.http.HttpHost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.{RestClient, RestClientBuilder, RestHighLevelClient}

object ProntoSink {

  var restHighLevelClient: RestHighLevelClient = _

  def getRestHighLevelClientInstance(): RestHighLevelClient = {
    if (restHighLevelClient == null){
      val restClientBuilder: RestClientBuilder = RestClient.builder(new HttpHost("estdq-datalvs.vip.ebay.com", 443, "https"))
      val basicCredentialsProvider = new BasicCredentialsProvider
      basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("76361a1177564b72bdba1ddb23d17c58", "1tIU7GQSADe4bCGGifWLfYYJSZGA8Szlc3ZWDMnd3gMhk43XPLUpw9Mv5viYQZ73"))
      restClientBuilder.setHttpClientConfigCallback((httpAsyncClientBuilder: HttpAsyncClientBuilder)=>{
        httpAsyncClientBuilder.setDefaultCredentialsProvider(basicCredentialsProvider)
      })
      restClientBuilder.setRequestConfigCallback(requestConfigBuilder => requestConfigBuilder.setConnectionRequestTimeout(1000 * 5).setSocketTimeout(1000 * 60))
      restHighLevelClient = new RestHighLevelClient(restClientBuilder)
    }
    restHighLevelClient
  }
}
