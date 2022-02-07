package com.ebay.dataquality.metric;

import com.ebay.dataquality.metric.exception.MetricBaseException;

import java.util.List;
import java.util.Map;

/**
 * Common interface for ProfileResult storage and access.
 * Repository provides methods to persist/access ProfileResults
 */
public interface ProfilerResultRepository {

    /**
     * Save the given profiler results into the backend storage.
     *
     * @param results
     * @throws MetricBaseException - if any error happens
     */
    void save(List<ProfilerResult> results) throws MetricBaseException;

    /**
     * Query profiler results from backend storage engine which matches the given parameters.
     * the query logic is like below:
     * timestamp >= args.from and timestamp <= args.to
     * AND (args.configName is not empty && configName == args.configName)
     * AND (args.metricName is not empty && name == args.metricName)
     * AND (args.operator is not empty && operator == args.operator)
     * AND (args.kvs is not empty && containsAll(tags,args.kvs))
     * <p>
     * <p>
     * containsAll logic:
     * for each (key1, value1) in args.kvs:
     * tags has an entry(key2,value2) where key1 == key2 and value1 == value2
     *
     * @param from       -- start time of the search range, inclusive, mandatory
     * @param to         -- end timestamp of the search range, inclusive, mandatory
     * @param configName --  config the profiler result generated from. - optional, empty of null means all
     * @param metricName -- name of the profiler result - optional
     * @param operator   -- operator of the profiler result - opational
     * @param kvs        -- tags kv of the profiler result - optional
     * @return matched profiler results
     */
    List<ProfilerResult> query(long from, long to, String configName, String metricName, String operator, Map<String, String> kvs);
}
