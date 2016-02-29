package com.sangupta.dryredis;

import java.util.Set;

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
    public void testSDIFF() {
        DryRedisSet redis = new DryRedisSet();
        
        redis.sadd("key1", "value1");
        redis.sadd("key1", "value2");
        redis.sadd("key1", "value3");
        
        redis.sadd("key2", "value4");
        redis.sadd("key2", "value5");
        
        Set<String> result = redis.sdiff("key1", "key2");
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(TestDryRedisUtils.asSet("value1", "value2", "value3"), result);
                
        // remove keys
        redis.sadd("key2", "value1");
        result = redis.sdiff("key1", "key2");
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(TestDryRedisUtils.asSet("value2", "value3"), result);
        
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
        DryRedisSet redis = new DryRedisSet();
        
        redis.sadd("key1", "value1");
        redis.sadd("key1", "value2");
        redis.sadd("key1", "value3");
        
        redis.sadd("key2", "value4");
        redis.sadd("key2", "value5");
        
        Assert.assertEquals(3, redis.sdiffstore("result", "key1", "key2"));
        Assert.assertEquals(TestDryRedisUtils.asSet("value1", "value2", "value3"), redis.smembers("result"));
                
        // remove keys
        redis.sadd("key2", "value1");
        Assert.assertEquals(2, redis.sdiffstore("result", "key1", "key2"));
        Assert.assertEquals(TestDryRedisUtils.asSet("value2", "value3"), redis.smembers("result"));
        
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
    public void SINTER() {
        DryRedisSet redis = new DryRedisSet();
        
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
        Assert.assertEquals(TestDryRedisUtils.asSet("value1"), result);
        
        redis.sadd("key2", "value2");
        redis.sadd("key2", "value3");
        result = redis.sinter("key1", "key2");
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(TestDryRedisUtils.asSet("value1", "value2", "value3"), result);
        
        result = redis.sinter("key2", "non-existent");
        Assert.assertEquals(0, result.size());
        
        result = redis.sinter("non-existent", "key2");
        Assert.assertEquals(0, result.size());
    }
    
    @Test
    public void testSINTERINSTORE() {
        DryRedisSet redis = new DryRedisSet();
        
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
        Assert.assertEquals(TestDryRedisUtils.asSet("value1"), redis.smembers("result"));
        
        redis.sadd("key2", "value2");
        redis.sadd("key2", "value3");
        Assert.assertEquals(3, redis.sinterstore("result", "key1", "key2"));
        Assert.assertEquals(TestDryRedisUtils.asSet("value1", "value2", "value3"), redis.smembers("result"));
        
        Assert.assertEquals(0, redis.sinterstore("result", "key2", "non-existent"));
        Assert.assertEquals(0, redis.scard("result"));
        
        Assert.assertEquals(0, redis.sinterstore("result", "non-existent", "key2"));
        Assert.assertEquals(0, redis.scard("result"));
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
