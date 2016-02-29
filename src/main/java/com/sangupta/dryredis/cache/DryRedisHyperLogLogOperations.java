package com.sangupta.dryredis.cache;

import java.util.List;

public interface DryRedisHyperLogLogOperations {

    int pfadd(String key);

    int pfadd(String key, String element);

    long pfcount(String key);

    long pfcount(List<String> keys);

    String pfmerge(String destination, List<String> keys);

}