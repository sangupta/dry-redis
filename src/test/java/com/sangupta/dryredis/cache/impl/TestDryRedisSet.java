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

package com.sangupta.dryredis.cache.impl;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.dryredis.TestUtils;
import com.sangupta.dryredis.cache.DryRedisSetOperations;

public class TestDryRedisSet {

    @Test
    public void testSADD() {
        DryRedisSetOperations redis = getRedis();
        
        Assert.assertEquals(1, redis.sadd("key", "value1"));
        Assert.assertEquals(1, redis.sadd("key", "value2"));
        Assert.assertEquals(1, redis.sadd("key", "value3"));
        Assert.assertEquals(0, redis.sadd("key", "value1"));
        Assert.assertEquals(0, redis.sadd("key", "value2"));
        
        Assert.assertEquals(1, redis.sadd("key1", TestUtils.asList("value1")));
        Assert.assertEquals(3, redis.sadd("key1", TestUtils.asList("value2", "value3", "value4")));
        Assert.assertEquals(2, redis.sadd("key1", TestUtils.asList("value5", "value3", "value6")));
    }
    
    @Test
    public void testSCARD() {
        DryRedisSetOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.scard("key"));
        redis.sadd("key", "value1");
        Assert.assertEquals(1, redis.scard("key"));
        redis.sadd("key", "value2");
        Assert.assertEquals(2, redis.scard("key"));
        redis.sadd("key", "value3");
        Assert.assertEquals(3, redis.scard("key"));
        redis.sadd("key", "value1");
        Assert.assertEquals(3, redis.scard("key"));
        redis.sadd("key", "value2");
        Assert.assertEquals(3, redis.scard("key"));
    }
    
    @Test
    public void testSDIFF() {
        DryRedisSetOperations redis = getRedis();
        
        redis.sadd("key1", "value1");
        redis.sadd("key1", "value2");
        redis.sadd("key1", "value3");
        
        redis.sadd("key2", "value4");
        redis.sadd("key2", "value5");
        
        Set<String> result = redis.sdiff("key1", "key2");
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(TestUtils.asSet("value1", "value2", "value3"), result);
                
        // remove keys
        redis.sadd("key2", "value1");
        result = redis.sdiff("key1", "key2");
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(TestUtils.asSet("value2", "value3"), result);
        
        redis.sadd("key2", "value2");
        redis.sadd("key2", "value3");
        result = redis.sdiff("key1", "key2");
        Assert.assertEquals(0, result.size());
        
        result = redis.sdiff("key2", "non-existent");
        Assert.assertEquals(5, result.size());
        
        result = redis.sdiff("non-existent", "key2");
        Assert.assertEquals(0, result.size());
    }
    
    @Test
    public void testSDIFFINSTORE() {
        DryRedisSetOperations redis = getRedis();
        
        redis.sadd("key1", "value1");
        redis.sadd("key1", "value2");
        redis.sadd("key1", "value3");
        
        redis.sadd("key2", "value4");
        redis.sadd("key2", "value5");
        
        Assert.assertEquals(3, redis.sdiffstore("result", "key1", "key2"));
        Assert.assertEquals(TestUtils.asSet("value1", "value2", "value3"), redis.smembers("result"));
                
        // remove keys
        redis.sadd("key2", "value1");
        Assert.assertEquals(2, redis.sdiffstore("result", "key1", "key2"));
        Assert.assertEquals(TestUtils.asSet("value2", "value3"), redis.smembers("result"));
        
        redis.sadd("key2", "value2");
        redis.sadd("key2", "value3");
        Assert.assertEquals(0, redis.sdiffstore("result", "key1", "key2"));
        Assert.assertEquals(0, redis.scard("result"));
        
        Assert.assertEquals(5, redis.sdiffstore("result", "key2", "non-existent"));
        
        Assert.assertEquals(0, redis.sdiffstore("result", "non-existent", "key2"));
        Assert.assertEquals(0, redis.scard("result"));
    }
    
    @Test
    public void testSISMEMBER() {
        DryRedisSetOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.sismember("key", "value1"));

        redis.sadd("key", "value1");
        Assert.assertEquals(1, redis.sismember("key", "value1"));
        Assert.assertEquals(0, redis.sismember("key", "value2"));
        
        redis.sadd("key", "value2");
        Assert.assertEquals(1, redis.sismember("key", "value1"));
        Assert.assertEquals(1, redis.sismember("key", "value2"));
        Assert.assertEquals(0, redis.sismember("key", "value3"));

        redis.sadd("key", "value3");
        Assert.assertEquals(1, redis.sismember("key", "value1"));
        Assert.assertEquals(1, redis.sismember("key", "value2"));
        Assert.assertEquals(1, redis.sismember("key", "value3"));
    }
    
    @Test
    public void SINTER() {
        DryRedisSetOperations redis = getRedis();
        
        redis.sadd("key1", "value1");
        redis.sadd("key1", "value2");
        redis.sadd("key1", "value3");
        
        redis.sadd("key2", "value4");
        redis.sadd("key2", "value5");
        
        Set<String> result = redis.sinter("key1", "key2");
        Assert.assertEquals(0, result.size());
                
        // remove keys
        redis.sadd("key2", "value1");
        result = redis.sinter("key1", "key2");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(TestUtils.asSet("value1"), result);
        
        redis.sadd("key2", "value2");
        redis.sadd("key2", "value3");
        result = redis.sinter("key1", "key2");
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(TestUtils.asSet("value1", "value2", "value3"), result);
        
        result = redis.sinter("key2", "non-existent");
        Assert.assertEquals(0, result.size());
        
        result = redis.sinter("non-existent", "key2");
        Assert.assertEquals(0, result.size());
    }
    
    @Test
    public void testSINTERINSTORE() {
        DryRedisSetOperations redis = getRedis();
        
        redis.sadd("key1", "value1");
        redis.sadd("key1", "value2");
        redis.sadd("key1", "value3");
        
        redis.sadd("key2", "value4");
        redis.sadd("key2", "value5");
        
        Assert.assertEquals(0, redis.sinterstore("result", "key1", "key2"));
        Assert.assertEquals(0, redis.scard("result"));
                
        // remove keys
        redis.sadd("key2", "value1");
        Assert.assertEquals(1, redis.sinterstore("result", "key1", "key2"));
        Assert.assertEquals(TestUtils.asSet("value1"), redis.smembers("result"));
        
        redis.sadd("key2", "value2");
        redis.sadd("key2", "value3");
        Assert.assertEquals(3, redis.sinterstore("result", "key1", "key2"));
        Assert.assertEquals(TestUtils.asSet("value1", "value2", "value3"), redis.smembers("result"));
        
        Assert.assertEquals(0, redis.sinterstore("result", "key2", "non-existent"));
        Assert.assertEquals(0, redis.scard("result"));
        
        Assert.assertEquals(0, redis.sinterstore("result", "non-existent", "key2"));
        Assert.assertEquals(0, redis.scard("result"));
    }

    @Test
    public void testSMEMBERS() {
        DryRedisSetOperations redis = getRedis();
        
        Assert.assertNull(redis.smembers("key"));

        redis.sadd("key", "value1");
        Assert.assertEquals(TestUtils.asSet("value1"), redis.smembers("key"));
        
        redis.sadd("key", "value2");
        Assert.assertEquals(TestUtils.asSet("value1", "value2"), redis.smembers("key"));

        redis.sadd("key", "value3");
        Assert.assertEquals(TestUtils.asSet("value1", "value2", "value3"), redis.smembers("key"));
    }
    
    @Test
    public void testSPOP() {
        DryRedisSetOperations redis = getRedis();
        
        // no key
        Assert.assertNull(redis.spop("non-existent"));
        
        // valid cases
        redis.sadd("key", "value1");
        redis.sadd("key", "value2");
        redis.sadd("key", "value3");
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.spop("key")));
        Assert.assertEquals(2, redis.scard("key"));
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.spop("key")));
        Assert.assertEquals(1, redis.scard("key"));
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.spop("key")));
        Assert.assertEquals(0, redis.scard("key"));
        
        // with count
        redis.sadd("key", "value1");
        redis.sadd("key", "value2");
        redis.sadd("key", "value3");
        redis.sadd("key", "value4");
        redis.sadd("key", "value5");
        redis.sadd("key", "value6");
        redis.sadd("key", "value7");
        redis.sadd("key", "value8");
        
        Assert.assertEquals(8, redis.scard("key"));
        
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.spop("key", 2)));
        Assert.assertEquals(6, redis.scard("key"));
        
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.spop("key", 3)));
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.spop("key", 2)));
        Assert.assertEquals(1, redis.scard("key"));
        
        // no key
        Assert.assertNull(redis.spop("non-existent", 5));
        
        // more count
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.spop("key", 5)));
    }

    @Test
    public void testSMOVE() {
        DryRedisSetOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.smove("non-existent", "destination", "hello"));

        redis.sadd("no-member", "value");
        Assert.assertEquals(0, redis.smove("no-member", "destination", "hello"));
        
        redis.sadd("key", "value");
        Assert.assertEquals(1, redis.scard("key"));
        Assert.assertEquals(0, redis.scard("destination"));
        Assert.assertEquals(1, redis.smove("key", "destination", "value"));
        Assert.assertEquals(0, redis.sismember("key", "value"));
        Assert.assertEquals(1, redis.sismember("destination", "value"));
        Assert.assertEquals(0, redis.scard("key"));
        Assert.assertEquals(1, redis.scard("destination"));
    }
    
    @Test
    public void testSREM() {
        DryRedisSetOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.srem("non-existent", "value"));
        
        redis.sadd("key", "value");
        Assert.assertEquals(0, redis.srem("key", "non-value"));
        Assert.assertEquals(1, redis.srem("key", "value"));
        Assert.assertEquals(0, redis.scard("key"));
        
        // multi
        redis.sadd("key", "value1");
        redis.sadd("key", "value2");
        redis.sadd("key", "value3");
        redis.sadd("key", "value4");
        redis.sadd("key", "value5");
        redis.sadd("key", "value6");
        redis.sadd("key", "value7");
        redis.sadd("key", "value8");
        
        Assert.assertEquals(8, redis.scard("key"));
        Assert.assertEquals(3, redis.srem("key", TestUtils.asList("value1", "value2", "v", "value3")));
        Assert.assertEquals(5, redis.scard("key"));
    }
    
    @Test
    public void testSUNION() {
        DryRedisSetOperations redis = getRedis();
        
        redis.sadd("key1", "value1");
        redis.sadd("key1", "value2");
        redis.sadd("key1", "value3");
        
        redis.sadd("key2", "value4");
        redis.sadd("key2", "value5");
        
        Set<String> result = redis.sunion("key1", "key2");
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(TestUtils.asSet("value1", "value2", "value3", "value4", "value5"), result);
                
        // remove keys
        redis.sadd("key2", "value1");
        result = redis.sunion("key1", "key2");
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(TestUtils.asSet("value1", "value2", "value3", "value4", "value5"), result);
        
        redis.sadd("key2", "value2");
        redis.sadd("key2", "value3");
        result = redis.sunion("key1", "key2");
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(TestUtils.asSet("value1", "value2", "value3", "value4", "value5"), result);
        
        result = redis.sunion("key2", "non-existent");
        Assert.assertEquals(5, result.size());
        
        result = redis.sunion("non-existent", "key2");
        Assert.assertEquals(5, result.size());
    }
    
    @Test
    public void testSUNIONSTORE() {
        DryRedisSetOperations redis = getRedis();
        
        redis.sadd("key1", "value1");
        redis.sadd("key1", "value2");
        redis.sadd("key1", "value3");
        
        redis.sadd("key2", "value4");
        redis.sadd("key2", "value5");
        
        Assert.assertEquals(5, redis.sunionstore("result", "key1", "key2"));
        Assert.assertEquals(3, redis.scard("key1"));
        Assert.assertEquals(2, redis.scard("key2"));
        Assert.assertEquals(5, redis.scard("result"));
        
        Assert.assertEquals(TestUtils.asSet("value1", "value2", "value3", "value4", "value5"), redis.smembers("result"));
                
        Assert.assertEquals(2, redis.sunionstore("result", "key2", "non-existent"));
        Assert.assertEquals(2, redis.scard("result"));
        
        Assert.assertEquals(2, redis.sunionstore("result", "non-existent", "key2"));
        Assert.assertEquals(2, redis.scard("result"));
    }
    
    @Test
    public void SRANDMEMBER() {
        DryRedisSetOperations redis = getRedis();
        
        Assert.assertNull(redis.srandmember("non-existent"));
        Assert.assertNull(redis.srandmember("non-existent", 5));
        
        // valid cases
        redis.sadd("key", "value1");
        redis.sadd("key", "value2");
        redis.sadd("key", "value3");
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.srandmember("key")));
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.srandmember("key")));
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.srandmember("key")));
        Assert.assertEquals(3, redis.scard("key"));
        
        // with count
        redis.sadd("key", "value1");
        redis.sadd("key", "value2");
        redis.sadd("key", "value3");
        redis.sadd("key", "value4");
        redis.sadd("key", "value5");
        redis.sadd("key", "value6");
        redis.sadd("key", "value7");
        redis.sadd("key", "value8");
        
        Assert.assertEquals(8, redis.scard("key"));
        
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.srandmember("key", 2)));
        Assert.assertEquals(8, redis.scard("key"));
        
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.srandmember("key", 3)));
        Assert.assertEquals(8, redis.scard("key"));
        
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.srandmember("key", 2)));
        Assert.assertEquals(8, redis.scard("key"));
        
        // no key
        Assert.assertNull(redis.srandmember("non-existent", 5));
        
        // more count
        Assert.assertTrue(TestUtils.contains(redis.smembers("key"), redis.srandmember("key", 5)));
    }
    
    protected DryRedisSetOperations getRedis() {
        return new DryRedisSet();
    }
    
}
