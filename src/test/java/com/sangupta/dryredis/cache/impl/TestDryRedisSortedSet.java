package com.sangupta.dryredis.cache.impl;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.dryredis.TestUtils;
import com.sangupta.dryredis.cache.DryRedisSortedSetOperations;

public class TestDryRedisSortedSet {
    
    @Test
    public void testZADD() {
        DryRedisSortedSetOperations redis = new DryRedisSortedSet();
        
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
        DryRedisSortedSetOperations redis = new DryRedisSortedSet();
        
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
        DryRedisSortedSetOperations redis = new DryRedisSortedSet();
        
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
    
    // helper methods

    protected DryRedisSortedSetOperations getRedis() {
        return new DryRedisSortedSet();
    }

}
