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
        
        Assert.assertEquals(1, redis.sadd("key1", TestDryRedisUtils.asList("value1")));
        Assert.assertEquals(3, redis.sadd("key1", TestDryRedisUtils.asList("value2", "value3", "value4")));
        Assert.assertEquals(2, redis.sadd("key1", TestDryRedisUtils.asList("value5", "value3", "value6")));
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
        
        // no key
        Assert.assertNull(redis.spop("non-existent"));
        
        // valid cases
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
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.spop("key", 2)));
        Assert.assertEquals(6, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.spop("key", 3)));
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.spop("key", 2)));
        Assert.assertEquals(1, redis.scard("key"));
        
        // no key
        Assert.assertNull(redis.spop("non-existent", 5));
        
        // more count
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.spop("key", 5)));
    }

    @Test
    public void testSMOVE() {
        DryRedisSet redis = new DryRedisSet();
        
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
        DryRedisSet redis = new DryRedisSet();
        
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
        Assert.assertEquals(3, redis.srem("key", TestDryRedisUtils.asList("value1", "value2", "v", "value3")));
        Assert.assertEquals(5, redis.scard("key"));
    }
    
    @Test
    public void testSUNION() {
        DryRedisSet redis = new DryRedisSet();
        
        redis.sadd("key1", "value1");
        redis.sadd("key1", "value2");
        redis.sadd("key1", "value3");
        
        redis.sadd("key2", "value4");
        redis.sadd("key2", "value5");
        
        Set<String> result = redis.sunion("key1", "key2");
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(TestDryRedisUtils.asSet("value1", "value2", "value3", "value4", "value5"), result);
                
        // remove keys
        redis.sadd("key2", "value1");
        result = redis.sunion("key1", "key2");
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(TestDryRedisUtils.asSet("value1", "value2", "value3", "value4", "value5"), result);
        
        redis.sadd("key2", "value2");
        redis.sadd("key2", "value3");
        result = redis.sunion("key1", "key2");
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(TestDryRedisUtils.asSet("value1", "value2", "value3", "value4", "value5"), result);
        
        result = redis.sunion("key2", "non-existent");
        Assert.assertEquals(5, result.size());
        
        result = redis.sunion("non-existent", "key2");
        Assert.assertEquals(5, result.size());
    }
    
    @Test
    public void testSUNIONSTORE() {
        DryRedisSet redis = new DryRedisSet();
        
        redis.sadd("key1", "value1");
        redis.sadd("key1", "value2");
        redis.sadd("key1", "value3");
        
        redis.sadd("key2", "value4");
        redis.sadd("key2", "value5");
        
        Assert.assertEquals(5, redis.sunionstore("result", "key1", "key2"));
        Assert.assertEquals(3, redis.scard("key1"));
        Assert.assertEquals(2, redis.scard("key2"));
        Assert.assertEquals(5, redis.scard("result"));
        
        Assert.assertEquals(TestDryRedisUtils.asSet("value1", "value2", "value3", "value4", "value5"), redis.smembers("result"));
                
        Assert.assertEquals(2, redis.sunionstore("result", "key2", "non-existent"));
        Assert.assertEquals(2, redis.scard("result"));
        
        Assert.assertEquals(2, redis.sunionstore("result", "non-existent", "key2"));
        Assert.assertEquals(2, redis.scard("result"));
    }
    
    @Test
    public void SRANDMEMBER() {
        DryRedisSet redis = new DryRedisSet();
        
        Assert.assertNull(redis.srandmember("non-existent"));
        Assert.assertNull(redis.srandmember("non-existent", 5));
        
        // valid cases
        redis.sadd("key", "value1");
        redis.sadd("key", "value2");
        redis.sadd("key", "value3");
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.srandmember("key")));
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.srandmember("key")));
        Assert.assertEquals(3, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.srandmember("key")));
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
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.srandmember("key", 2)));
        Assert.assertEquals(8, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.srandmember("key", 3)));
        Assert.assertEquals(8, redis.scard("key"));
        
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.srandmember("key", 2)));
        Assert.assertEquals(8, redis.scard("key"));
        
        // no key
        Assert.assertNull(redis.srandmember("non-existent", 5));
        
        // more count
        Assert.assertTrue(TestDryRedisUtils.contains(redis.smembers("key"), redis.srandmember("key", 5)));
    }
}
