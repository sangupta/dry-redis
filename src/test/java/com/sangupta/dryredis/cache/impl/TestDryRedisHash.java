package com.sangupta.dryredis.cache.impl;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.dryredis.TestUtils;
import com.sangupta.dryredis.cache.DryRedisHashOperations;

public class TestDryRedisHash {

    @Test
    public void testHDEL() {
        DryRedisHashOperations redis = getRedis();
        
        Assert.assertEquals(0, redis.hdel("non-existent", "field"));
        Assert.assertEquals(0, redis.hdel("key", "field"));
        
        redis.hset("key", "field", "value");
        Assert.assertEquals(1, redis.hdel("key", "field"));
        
        redis.hset("key", "field", "value");
        redis.hset("key", "field1", "value1");
        redis.hset("key", "field2", "value2");
        redis.hset("key", "field3", "value3");
        Assert.assertEquals(3, redis.hdel("key", TestUtils.asList("field", "field1", "field5", "field6", "field3")));
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
        Assert.assertEquals(TestUtils.asList("field1", "field2"), redis.hkeys("key"));
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
    
    private DryRedisHashOperations getRedis() {
        return new DryRedisHash();
    }
    
}