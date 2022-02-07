package com.ebay.dataquality;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicConfigTypeFactory {
    public final static DynamicConfigTypeFactory SOURCE = new DynamicConfigTypeFactory();
    public final static DynamicConfigTypeFactory RULE = new DynamicConfigTypeFactory();
    public final static DynamicConfigTypeFactory TRANSFORMATION_EXPRESSION = new DynamicConfigTypeFactory();
    public final static DynamicConfigTypeFactory PROFILER_EXPRESSION = new DynamicConfigTypeFactory();
    public final static DynamicConfigTypeFactory SINK = new DynamicConfigTypeFactory();

    static {
    }


    private Map<String, DynamicConfigType> cache = new ConcurrentHashMap<>();

    private DynamicConfigTypeFactory() {
    }

    public Optional<DynamicConfigType> fromType(String type) {
        DynamicConfigType dct = cache.get(type);
        return Optional.ofNullable(dct);
    }

    public void register(DynamicConfigType dct) {
        cache.put(dct.getType(), dct);
    }

    public Collection<DynamicConfigType> allTypes() {
        return cache.values();
    }
}
