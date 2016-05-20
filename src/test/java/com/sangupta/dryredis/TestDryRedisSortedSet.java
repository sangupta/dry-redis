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

package com.sangupta.dryredis;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.dryredis.support.DryRedisSetAggregationType;

/**
 * Unit tests for {@link DryRedisSortedSet}.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisSortedSet {
    
    @Test
    public void testZADD() {
        DryRedisSortedSetOperations redis = getRedis();
        
        Assert.assertEquals(1, redis.zadd("key", 0, "a"));
        Assert.assertEquals(1, redis.zadd("key", 0, "b"));
        Assert.assertEquals(1, redis.zadd("key", 0, "c"));
        Assert.assertEquals(1, redis.zadd("key", 0, "d"));
        Assert.assertEquals(1, redis.zadd("key", 0, "e"));
        
        Assert.assertEquals(0, redis.zadd("key", 0, "a"));
        Assert.assertEquals(0, redis.zadd("key", 0, "b"));
        Assert.assertEquals(0, redis.zadd("key", 0, "c"));
        Assert.assertEquals(0, redis.zadd("key", 0, "d"));
        Assert.assertEquals(0, redis.zadd("key", 0, "e"));
    }
    
    @Test
    public void testZREVRANGE() {
        DryRedisSortedSetOperations redis = getRedis();
        
        redis.zadd("key", 1, "one");
        redis.zadd("key", 2, "two");
        redis.zadd("key", 3, "three");
        
        Assert.assertEquals(TestUtils.asList("three", "two", "one"), redis.zrevrange("key", 0, -1, false));
        Assert.assertEquals(TestUtils.asList("one"), redis.zrevrange("key", 2, 3, false));
        Assert.assertEquals(TestUtils.asList("two", "one"), redis.zrevrange("key", -2, -1, false));
    }
    
    @Test
    public void testZREVRANK() {
        DryRedisSortedSetOperations redis = getRedis();
        
        redis.zadd("key", 1, "one");
        redis.zadd("key", 2, "two");
        redis.zadd("key", 3, "three");
        
        Assert.assertEquals((Integer) 2, redis.zrevrank("key", "one"));
        Assert.assertEquals((Integer) null, redis.zrevrank("key", "four"));
    }
    
    @Test
    public void testZCARD() {
        DryRedisSortedSetOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.zcard("key"));
        
        redis.zadd("key", 0, "v1");
        Assert.assertEquals(1, redis.zcard("key"));
        
        redis.zadd("key", 0, "v1");
        Assert.assertEquals(1, redis.zcard("key"));
        
        redis.zadd("key", 1, "v1");
        Assert.assertEquals(1, redis.zcard("key"));
    }
    
    @Test
    public void testZLEXCOUNT() {
        DryRedisSortedSetOperations redis = getRedis();
        
        redis.zadd("key", 0, "a");
        redis.zadd("key", 0, "b");
        redis.zadd("key", 0, "c");
        redis.zadd("key", 0, "d");
        redis.zadd("key", 0, "e");
        redis.zadd("key", 0, "f");
        redis.zadd("key", 0, "g");
        
        Assert.assertEquals(3, redis.zlexcount("key", "(b", "(f"));
        Assert.assertEquals(4, redis.zlexcount("key", "[b", "(f"));
        Assert.assertEquals(5, redis.zlexcount("key", "[b", "[f"));
        
        Assert.assertEquals(7, redis.zlexcount("key", "-", "+"));
        Assert.assertEquals(0, redis.zlexcount("key", "+", "-"));
    }
    
    @Test
    public void testZRANGEBYLEX() {
        DryRedisSortedSetOperations redis = getRedis();
        
        redis.zadd("key", 0, "a");
        redis.zadd("key", 0, "b");
        redis.zadd("key", 0, "c");
        redis.zadd("key", 0, "d");
        redis.zadd("key", 0, "e");
        redis.zadd("key", 0, "f");
        redis.zadd("key", 0, "g");
        
        Assert.assertEquals(TestUtils.asList("a", "b", "c", "d", "e", "f", "g"), redis.zrangebylex("key", "-", "+"));
        Assert.assertEquals(TestUtils.asList("a", "b", "c"), redis.zrangebylex("key", "-", "[c"));
        Assert.assertEquals(TestUtils.asList("b", "c", "d", "e"), redis.zrangebylex("key", "[aaa", "(f"));
    }
    
    @Test
    public void testZINTERSTORE() {
        DryRedisSortedSetOperations redis = getRedis();
        
        // same keys
        redis.zadd("key1", 1, "a");
        redis.zadd("key1", 2, "b");
        
        redis.zadd("key2", 3, "a");
        redis.zadd("key2", 4, "b");
        
        redis.zadd("key3", 3, "c");
        
        // sum aggregate
        Assert.assertEquals(2, redis.zinterstore("result", TestUtils.asList("key1", "key2"), new double[] { 2.0d, 3.0d }, DryRedisSetAggregationType.SUM));
        Assert.assertTrue(TestUtils.equalUnsorted(TestUtils.asList("a", "b"), redis.zrange("result", 0, 10, false)));
        Assert.assertEquals(11.0d, redis.zscore("result", "a"), 0d);
        Assert.assertEquals(16.0d, redis.zscore("result", "b"), 0d);
        Assert.assertEquals(1.0d, redis.zscore("key1", "a"), 0d);
        Assert.assertEquals(2.0d, redis.zscore("key1", "b"), 0d);
        Assert.assertEquals(3.0d, redis.zscore("key2", "a"), 0d);
        Assert.assertEquals(4.0d, redis.zscore("key2", "b"), 0d);
        
        // min aggregate
        Assert.assertEquals(2, redis.zinterstore("result", TestUtils.asList("key1", "key2"), new double[] { 2.0d, 3.0d }, DryRedisSetAggregationType.MIN));
        Assert.assertTrue(TestUtils.equalUnsorted(TestUtils.asList("a", "b"), redis.zrange("result", 0, 10, false)));
        Assert.assertEquals(2.0d, redis.zscore("result", "a"), 0d);
        Assert.assertEquals(4.0d, redis.zscore("result", "b"), 0d);
        
        // min aggregate
        Assert.assertEquals(2, redis.zinterstore("result", TestUtils.asList("key1", "key2"), new double[] { 2.0d, 3.0d }, DryRedisSetAggregationType.MAX));
        Assert.assertTrue(TestUtils.equalUnsorted(TestUtils.asList("a", "b"), redis.zrange("result", 0, 10, false)));
        Assert.assertEquals(9.0d, redis.zscore("result", "a"), 0d);
        Assert.assertEquals(12.0d, redis.zscore("result", "b"), 0d);
    }
    
    // helper methods

    protected DryRedisSortedSetOperations getRedis() {
        return new DryRedisSortedSet();
    }

}
