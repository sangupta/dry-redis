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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class TestDryRedisHash {

    @Test
    public void testHDEL() {
        DryRedisHashOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.hdel("non-existent", "field"));
        Assert.assertEquals(0, redis.hdel("key", "field"));
        
        redis.hset("key", "field", "value");
        Assert.assertEquals(1, redis.hdel("key", "field"));
        Assert.assertEquals(0, redis.hdel("key", "field"));
        
        redis.hset("key", "field", "value");
        redis.hset("key", "field1", "value1");
        redis.hset("key", "field2", "value2");
        redis.hset("key", "field3", "value3");
        Assert.assertEquals(3, redis.hdel("key", TestUtils.asList("field", "field1", "field5", "field6", "field3")));
        
        Assert.assertEquals(0, redis.hdel("non-existent", TestUtils.asList("field", "field1", "field5", "field6", "field3")));
    }
    
    @Test
    public void testHEXISTS() {
        DryRedisHashOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.hexists("non-existent", "field"));
        
        redis.hset("key", "field", "value");
        Assert.assertEquals(1, redis.hexists("key", "field"));
        Assert.assertEquals(0, redis.hexists("key", "field1"));
    }
    
    @Test
    public void testHGET() {
        DryRedisHashOperations redis = getRedis();
        
        Assert.assertNull(redis.hget("key", "field"));
        
        redis.hset("key", "field", "value");
        Assert.assertNull(redis.hget("key", "field1"));
        
        Assert.assertEquals("value", redis.hget("key", "field"));
        redis.hset("key", "field", "value3");
        Assert.assertEquals("value3", redis.hget("key", "field"));
    }
    
    @Test
    public void testHKEYS() {
        DryRedisHashOperations redis = getRedis();
        
        Assert.assertNull(redis.hkeys("key"));
        
        redis.hset("key", "field1", "value1");
        redis.hset("key", "field2", "value1");
        TestUtils.equalUnsorted(TestUtils.asList("field1", "field2"), redis.hkeys("key"));
    }
    
    @Test
    public void testHLEN() {
        DryRedisHashOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.hlen("key"));
        
        redis.hset("key", "field1", "value1");
        redis.hset("key", "field2", "value1");
        Assert.assertEquals(2, redis.hlen("key"));
        redis.hset("key", "field2", "value2");
        Assert.assertEquals(2, redis.hlen("key"));
    }
    
    @Test
    public void testHSET() {
        DryRedisHashOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.hlen("key"));
        
        Assert.assertEquals(1, redis.hset("key", "field1", "value1"));
        Assert.assertEquals(1, redis.hset("key", "field2", "value1"));
        Assert.assertEquals(2, redis.hlen("key"));
        Assert.assertEquals(0, redis.hset("key", "field2", "value2"));
        Assert.assertEquals(2, redis.hlen("key"));
    }
    
    @Test
    public void testHSETNX() {
        DryRedisHashOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.hlen("key"));
        
        Assert.assertEquals(1, redis.hsetnx("key", "field1", "value1"));
        Assert.assertEquals(1, redis.hsetnx("key", "field2", "value1"));
        Assert.assertEquals(2, redis.hlen("key"));
        Assert.assertEquals(0, redis.hsetnx("key", "field2", "value2"));
        Assert.assertEquals(2, redis.hlen("key"));
    }
    
    @Test
    public void testHSTRLEN() {
        DryRedisHashOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.hstrlen("key", "field"));
        redis.hset("key", "field1", "value1");
        Assert.assertEquals(0, redis.hstrlen("key", "field"));
        Assert.assertEquals(6, redis.hstrlen("key", "field1"));
        redis.hset("key", "field1", "hello world");
        Assert.assertEquals(11, redis.hstrlen("key", "field1"));
    }
    
    @Test
    public void HVALS() {
        DryRedisHashOperations redis = getRedis();
        
        redis.hset("key", "field1", "value1");
        redis.hset("key", "field2", "value2");
        redis.hset("key", "field3", "value3");
        redis.hset("key", "field4", "value4");
        
        TestUtils.equalUnsorted(TestUtils.asList("value1", "value2", "value3", "value4"), redis.hvals("key"));
        
        // non-existent
        TestUtils.equalUnsorted(TestUtils.asList(""), redis.hvals("non-existent"));
        
        redis.hdel("key", "field1");
        redis.hdel("key", "field2");
        redis.hdel("key", "field3");
        redis.hdel("key", "field4");
        TestUtils.equalUnsorted(TestUtils.asList(""), redis.hvals("non-existent"));
    }
    
    @Test
    public void testHINCRBY() {
        DryRedisHashOperations redis = getRedis();
        
        // no key
        Assert.assertEquals(5, redis.hincrby("key", "test", 5));
        Assert.assertEquals(10, redis.hincrby("key", "test", 5));
        Assert.assertEquals(15, redis.hincrby("key", "test", 5));
        
        // previous key
        redis.hset("key", "test", "100");
        Assert.assertEquals(110, redis.hincrby("key", "test", 10));
        Assert.assertEquals(100, redis.hincrby("key", "test", -10));
    }
    
    @Test
    public void testHINCRBYFLOAT() {
        DryRedisHashOperations redis = getRedis();
        
        // no key
        Assert.assertEquals(5.1d, redis.hincrbyfloat("key", "test", 5.1d), 0.0001d);
        Assert.assertEquals(10.2d, redis.hincrbyfloat("key", "test", 5.1d), 0.0001d);
        Assert.assertEquals(15.3d, redis.hincrbyfloat("key", "test", 5.1d), 0.0001d);
        
        // previous key
        redis.hset("key", "test", "100.3");
        Assert.assertEquals(105.4d, redis.hincrbyfloat("key", "test", 5.1d), 0.0001d);
        Assert.assertEquals(110.5d, redis.hincrbyfloat("key", "test", 5.1d), 0.0001d);
    }
    
    @Test
    public void testHGETALL() {
        DryRedisHashOperations redis = getRedis();
        
        Map<String, String> map = new HashMap<String, String>();
        for(int index = 0; index < 1000; index++) {
            String field = UUID.randomUUID().toString();
            String value = UUID.randomUUID().toString();
            map.put(field, value);
            redis.hset("key", field, value);
        }
        
        // start testing
        List<String> list = redis.hgetall("key");
        for(int index = 0; index < list.size(); index += 2) {
            String key = list.get(index);
            String value = list.get(index + 1);
            
            Assert.assertTrue(map.containsKey(key));
            Assert.assertEquals(map.get(key), value);
            
            map.remove(key);
        }
        
        Assert.assertTrue(map.isEmpty());
    }
    
    @Test
    public void testHMGET() {
        DryRedisHashOperations redis = getRedis();
        
        Random random = new Random(); 
        List<String> keys = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        for(int index = 0; index < 1000; index++) {
            String field = UUID.randomUUID().toString();
            String value = UUID.randomUUID().toString();
            map.put(field, value);
            redis.hset("key", field, value);
            if(random.nextInt(100) > 50) {
                keys.add(field);
            }
        }
        
        // start testing
        List<String> list = redis.hmget("key", keys);
        Assert.assertEquals(keys.size(), list.size());
        
        for(int index = 0; index < list.size(); index++) {
            String key = keys.get(index);
            String value = list.get(index);
            
            Assert.assertEquals(map.get(key), value);
        }
    }
    
    @Test
    public void testHMSET() {
        DryRedisHashOperations redis = getRedis();
        
        Map<String, String> map = new HashMap<String, String>();
        for(int index = 0; index < 1000; index++) {
            String field = UUID.randomUUID().toString();
            String value = UUID.randomUUID().toString();
            map.put(field, value);
        }
        
        Assert.assertEquals("OK", redis.hmset("key", map));
        
        // start testing
        List<String> list = redis.hgetall("key");
        for(int index = 0; index < list.size(); index += 2) {
            String key = list.get(index);
            String value = list.get(index + 1);
            
            Assert.assertTrue(map.containsKey(key));
            Assert.assertEquals(map.get(key), value);
            
            map.remove(key);
        }
        
        Assert.assertTrue(map.isEmpty());
    }
    
    protected DryRedisHashOperations getRedis() {
        return new DryRedisHash();
    }
    
}
