package com.ebay.dataquality.metric;

import com.ebay.dataquality.DateTimeUtils;
import com.ebay.dataquality.metric.exception.MetricBaseException;
import com.ebay.dataquality.metric.exception.PersistMetricException;
import com.ebay.dataquality.metric.exception.QueryMetricException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@ToString
@Getter
public class ElasticSearchProfilerResultRepository implements ProfilerResultRepository {

    @Builder.Default
    private String indexTemplatePrefix = "profiling-test-";
    private RestHighLevelClient client;

    /**
     * doc id is composed of:
     * //timestamp + "-" + MD5(configname + "-" + name + "-" + profiler + join(tags(k=v), ","))
     *
     * @param result
     * @return
     */
    public String genDocId(ProfilerResult result) {
        Map<String, String> tags = result.getTags();
        if (tags == null) tags = new HashMap<String, String>();
        String strTag = tags.entrySet().stream().sorted(
                (o1, o2) -> o1.getKey().compareTo(o2.getKey())
        ).map(kv -> kv.getKey() + "=" + kv.getValue()).collect(Collectors.joining(","));
        String str = result.getConfigName() + "-" + result.getName() + "-" + result.getOperator() + "-" + strTag;
        String md5 = DigestUtils.md5Hex(str);
        return (result.getFrom() + "-" + result.getTo() + "-" + md5).toLowerCase();
    }

    private String calculateIndexDate(long ts) {
        return DateTimeUtils.MTS_YYYYMMDD.format(ts);
    }

    private String[] calculateIndexs(long from, long end) {
        Set<String> results = new HashSet<>();
        long next = from;
        while (end >= next) {
            results.add(indexTemplatePrefix + calculateIndexDate(next));
            next = next + 86400 * 1000;
        }
        results.add(indexTemplatePrefix + calculateIndexDate(end));
        return results.toArray(new String[results.size()]);
    }

    @Override
    public void save(List<ProfilerResult> results) throws MetricBaseException {
        boolean hasRequest = false;
        BulkRequest bulkRequest = new BulkRequest();
        for (ProfilerResult result : results) {
            String index = indexTemplatePrefix + calculateIndexDate(result.getFrom());
            IndexRequest request = new IndexRequest(index)
                    .id(genDocId(result))
                    .type("_doc")
                    .source(ProfilerResult.toJson(result), XContentType.JSON);

            bulkRequest.add(request);
            hasRequest = true;
        }

        if (hasRequest) {
            try {
                BulkResponse bulkResponse = this.client.bulk(bulkRequest, RequestOptions.DEFAULT);
                for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
                    if (bulkItemResponse.isFailed()) {
                        System.err.println(bulkItemResponse.getFailureMessage());
                    }
                }
            } catch (IOException e) {
                throw new PersistMetricException("failed to save ProfilerResult: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public List<ProfilerResult> query(long from, long to, String configName, String metricName, String operator, Map<String, String> kvs) {
        Validate.isTrue(from < to, "from must be earlier than to");
        Validate.isTrue(StringUtils.isNotEmpty(configName), "config name is required.");

        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder rootBuilder = QueryBuilders.boolQuery();

        rootBuilder.must(QueryBuilders.rangeQuery("from").gte(from));
        rootBuilder.must(QueryBuilders.rangeQuery("to").lt(to));
        rootBuilder.must(QueryBuilders.termQuery("configName", configName));

        if (!StringUtils.isEmpty(metricName)) {
            rootBuilder.must(QueryBuilders.termQuery("name", metricName));
        }
        if (!StringUtils.isEmpty(operator)) {
            rootBuilder.must(QueryBuilders.termQuery("operator", operator));
        }
        if (kvs != null && kvs.size() > 0) {
            for (Map.Entry<String, String> kv : kvs.entrySet()) {
                rootBuilder.must(QueryBuilders.termQuery("tags." + kv.getKey(), kv.getValue()));
            }
        }
        builder.query(rootBuilder);

        SearchRequest sr = new SearchRequest(calculateIndexs(from, to), builder);
        sr.indicesOptions(IndicesOptions.lenientExpandOpen());
        try {
            List<ProfilerResult> results = new ArrayList<>();
            SearchResponse searchResponse = client.search(sr, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            for (SearchHit searchHit : searchHits) {
                try {
                    ProfilerResult result = ProfilerResult.fromJsonText(searchHit.getSourceAsString());
                    results.add(result);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
            }
            return results;
        } catch (IOException e) {
            throw new QueryMetricException("failed to query ProfilerResult: " + e.getMessage(), e);
        }
    }
}
