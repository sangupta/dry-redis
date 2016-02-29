package com.sangupta.dryredis;

import org.junit.Assert;
import org.junit.Test;

public class TestDryRedisSet {

    @Test
    public void testSADD() {
        DryRedisSet redis = new DryRedisSet();
        
        Assert.assertEquals(1, redis.sadd("key", "value1"));
        Assert.assertEquals(1, redis.sadd("key", "value2"));
        Assert.assertEquals(1, redis.sadd("key", "value3"));
        Assert.assertEquals(0, redis.sadd("key", "value1"));
        Assert.assertEquals(0, redis.sadd("key", "value2"));
        
        // TODO: multi
    }
    
    @Test
    public void testSCARD() {
        DryRedisSet redis = new DryRedisSet();
        
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
    public void testSISMEMBER() {
        DryRedisSet redis = new DryRedisSet();
        
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
    public void testSMEMBERS() {
        DryRedisSet redis = new DryRedisSet();
        
        Assert.assertNull(redis.smembers("key"));

        redis.sadd("key", "value1");
        Assert.assertEquals(TestDryRedisUtils.asSet("value1"), redis.smembers("key"));
        
        redis.sadd("key", "value2");
        Assert.assertEquals(TestDryRedisUtils.asSet("value1", "value2"), redis.smembers("key"));

        redis.sadd("key", "value3");
        Assert.assertEquals(TestDryRedisUtils.asSet("value1", "value2", "value3"), redis.smembers("key"));
    }
    
    @Test
    public void testSPOP() {
        DryRedisSet redis = new DryRedisSet();
        
        redis.sadd("key", "value1");
        redis.sadd("key", "value2");
        redis.sadd("key", "value3");
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.spop("key")));
        Assert.assertEquals(2, redis.scard("key"));
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.spop("key")));
        Assert.assertEquals(1, redis.scard("key"));
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.spop("key")));
        Assert.assertEquals(0, redis.scard("key"));
        
        // with index
        redis.sadd("key", "value1");
        redis.sadd("key", "value2");
        redis.sadd("key", "value3");
        redis.sadd("key", "value4");
        redis.sadd("key", "value5");
        redis.sadd("key", "value6");
        redis.sadd("key", "value7");
        redis.sadd("key", "value8");
        
        Assert.assertEquals(8, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.spop("key", 2)));
        Assert.assertEquals(6, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.spop("key", 3)));
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.spop("key", 2)));
        Assert.assertEquals(1, redis.scard("key"));
    }
    
}
