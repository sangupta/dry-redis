package com.sangupta.dryredis;

import org.junit.Assert;
import org.junit.Test;

public class TestDryRedisKeys {

    @Test
    public void testDEL() {
        DryRedis redis = getRedis();
        
        Assert.assertEquals(0, redis.del("key*"));
        
        redis.set("key1", "value");
        redis.set("key2", "value");
        redis.set("key3", "value");
        
        Assert.assertEquals(0, redis.del("key4"));
        
        redis.set("key33", "value");
        Assert.assertEquals(2, redis.del("key3*"));
        Assert.assertEquals(0, redis.del("key3*"));
        
        redis.set("key3", "value");
        Assert.assertEquals(3, redis.del("ke*"));
    }
    
    @Test
    public void testEXPIRE() {
        
    }
    
    @Test
    public void testEXPIREAT() {
        
    }
    
    @Test
    public void testPEXPIRE() {
        
    }
    
    @Test
    public void testPEXPIREAT() {
        
    }
    
    @Test
    public void testRENAME() {
        DryRedis redis = getRedis();
        
        redis.set("key", "value");
        Assert.assertNull(redis.get("new-key"));
        Assert.assertEquals("OK", redis.rename("key", "new-key"));
        Assert.assertNull(redis.get("key"));
        Assert.assertNotNull(redis.get("new-key"));
        Assert.assertEquals("value", redis.get("new-key"));
        
        // error cases
        Assert.assertEquals("ERROR", redis.rename(null, "newKey"));
        Assert.assertEquals("ERROR", redis.rename("newKey", null));
        Assert.assertEquals("ERROR", redis.rename(null, null));
        
        Assert.assertEquals("ERROR", redis.rename("same-key", "same-key"));
        Assert.assertEquals("ERROR", redis.rename("same-key", "same-new-key"));
    }
    
    @Test
    public void testRENAMENX() {
        DryRedis redis = getRedis();
        
        redis.set("key", "value");
        Assert.assertNull(redis.get("new-key"));
        Assert.assertEquals(1, redis.renamenx("key", "new-key"));
        Assert.assertNull(redis.get("key"));
        Assert.assertNotNull(redis.get("new-key"));
        Assert.assertEquals("value", redis.get("new-key"));
        
        redis.set("key2", "value");
        Assert.assertEquals(0, redis.renamenx("key2", "new-key"));
        
        // error cases
        Assert.assertEquals(0, redis.renamenx(null, "newKey"));
        Assert.assertEquals(0, redis.renamenx("newKey", null));
        Assert.assertEquals(0, redis.renamenx(null, null));
        
        Assert.assertEquals(0, redis.renamenx("same-key", "same-key"));
        Assert.assertEquals(0, redis.renamenx("same-key", "same-new-key"));
    }
    
    @Test
    public void testKEYS() {
        
    }
    
    @Test
    public void testMIGRATE() {
        
    }
    
    @Test
    public void testMOVE() {
        
    }
    
    @Test
    public void testRANDOMKEY() {
        
    }
    
    @Test
    public void testTTL() {
        
    }
    
    @Test
    public void testPTTL() {
        
    }
    
    @Test
    public void testWAIT() {
        DryRedis redis = getRedis();
        
        Assert.assertEquals(0, redis.wait(0, 0l));
        Assert.assertEquals(0, redis.wait(10, 10l));
    }
    
    protected DryRedis getRedis() {
        DryRedis redis = DryRedis.getDatabase();
        redis.flushdb();
        return redis;
    }

}

