/**
 *
 * dry-redis: In-memory pure java implementation to Redis
 * Copyright (c) 2016, Sandeep Gupta
 * 
 * http://sangupta.com/projects/dry-redis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.sangupta.dryredis.cache;

import java.util.List;
import java.util.Set;

import com.sangupta.dryredis.support.DryRedisRangeArgument;
import com.sangupta.dryredis.support.DryRedisSetAggregationType;

public interface DryRedisSortedSetOperations {

    public int zadd(String key, double score, String member);

    public long zcard(String key);

    public long zcount(String key, double min, double max);

    public double zincrby(String key, double increment, String member);

    public Integer zrank(String key, String member);

    public Integer zrevrank(String key, String member);

    public int zrem(String key, String member);

    public int zrem(String key, Set<String> members);

    public Double zscore(String key, String member);

    public List<String> zrange(String key, int start, int stop, boolean withScores);

    public List<String> zrangebylex(String key, String min, String max);

    public List<String> zrangebylex(String key, DryRedisRangeArgument min, DryRedisRangeArgument max);

    public List<String> zrevrangebylex(String key, String min, String max);

    public List<String> zrevrangebylex(String key, DryRedisRangeArgument max, DryRedisRangeArgument min);

    public int zlexcount(String key, String min, String max);

    public int zlexcount(String key, DryRedisRangeArgument min, DryRedisRangeArgument max);

    public int zremrangebylex(String key, String min, String max);

    public int zremrangebylex(String key, DryRedisRangeArgument min, DryRedisRangeArgument max);

    public int zremrangebyrank(String key, int start, int stop);

    public int zremrangebyscore(String key, DryRedisRangeArgument min, DryRedisRangeArgument max);

    public List<String> zrevrange(String key, int start, int stop, boolean withScores);

    public int zinterstore(String destination, List<String> keys);

    public int zinterstore(String destination, List<String> keys, double[] weights, DryRedisSetAggregationType aggregation);

    public int zunionstore(String destination, List<String> keys);

    public int zunionstore(String destination, List<String> keys, double[] weights, DryRedisSetAggregationType aggregation);

}