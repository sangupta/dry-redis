package com.sangupta.dryredis.cache.impl;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.dryredis.TestUtils;

public class TestDryRedisSortedSet {
    
    @Test
    public void testZADD() {
        DryRedisSortedSet redis = new DryRedisSortedSet();
        
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
    public void testZLEXCOUNT() {
        DryRedisSortedSet redis = new DryRedisSortedSet();
        
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
        DryRedisSortedSet redis = new DryRedisSortedSet();
        
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

}
