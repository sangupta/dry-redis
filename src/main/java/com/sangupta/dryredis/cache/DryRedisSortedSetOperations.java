package com.sangupta.dryredis.cache;

import java.util.List;
import java.util.Set;

import com.sangupta.dryredis.support.DryRedisRangeArgument;
import com.sangupta.dryredis.support.DryRedisSetAggregationType;

public interface DryRedisSortedSetOperations {

    int zadd(String key, double score, String member);

    long zcard(String key);

    long zcount(String key, double min, double max);

    double zincrby(String key, double increment, String member);

    Integer zrank(String key, String member);

    Integer zrevrank(String key, String member);

    int zrem(String key, String member);

    int zrem(String key, Set<String> members);

    Double zscore(String key, String member);

    List<String> zrange(String key, int start, int stop, boolean withScores);

    List<String> zrangebylex(String key, String min, String max);

    List<String> zrangebylex(String key, DryRedisRangeArgument min, DryRedisRangeArgument max);

    List<String> zrevrangebylex(String key, String min, String max);

    List<String> zrevrangebylex(String key, DryRedisRangeArgument max, DryRedisRangeArgument min);

    int zlexcount(String key, String min, String max);

    int zlexcount(String key, DryRedisRangeArgument min, DryRedisRangeArgument max);

    int zremrangebylex(String key, String min, String max);

    int zremrangebylex(String key, DryRedisRangeArgument min, DryRedisRangeArgument max);

    int zremrangebyrank(String key, int start, int stop);

    int zremrangebyscore(String key, DryRedisRangeArgument min, DryRedisRangeArgument max);

    List<String> zrevrange(String key, int start, int stop, boolean withScores);

    int zinterstore(String destination, List<String> keys);

    int zinterstore(String destination, List<String> keys, double[] weights, DryRedisSetAggregationType aggregation);

    int zunionstore(String destination, List<String> keys);

    int zunionstore(String destination, List<String> keys, double[] weights, DryRedisSetAggregationType aggregation);

}