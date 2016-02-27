package com.sangupta.dryredis;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link DryRedisList}.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisList {
    
    final String listName = UUID.randomUUID().toString();
    
    @Test
    public void testLINDEX() {
        DryRedisList redis = new DryRedisList();
        
        // non existent
        Assert.assertNull(redis.lindex(listName, 0));
        Assert.assertNull(redis.lindex(listName, -1));
        Assert.assertNull(redis.lindex(listName, 1));
        
        Assert.assertEquals(1, redis.rpush(listName, "value1"));
        Assert.assertEquals(2, redis.rpush(listName, "value2"));
        Assert.assertEquals(3, redis.rpush(listName, "value3"));
        Assert.assertEquals("value1", redis.lindex(listName, 0));
        Assert.assertEquals("value2", redis.lindex(listName, 1));
        Assert.assertEquals("value3", redis.lindex(listName, 2));
        Assert.assertNull(redis.lindex(listName, 3));
        Assert.assertEquals("value1", redis.lindex(listName, 0));
        Assert.assertEquals("value3", redis.lindex(listName, -1));
        Assert.assertEquals("value2", redis.lindex(listName, -2));
    }
    
    @Test
    public void testLLEN() {
        DryRedisList redis = new DryRedisList();
        
        Assert.assertEquals(0, redis.llen(listName));
       
        Assert.assertEquals(1, redis.rpush(listName, "value1"));
        Assert.assertEquals(1, redis.llen(listName));
        
        Assert.assertEquals(2, redis.rpush(listName, "value1"));
        Assert.assertEquals(2, redis.llen(listName));
        
        Assert.assertEquals(3, redis.rpush(listName, "value1"));
        Assert.assertEquals(3, redis.llen(listName));
        
        redis.lpop(listName);
        Assert.assertEquals(2, redis.llen(listName));
        
        redis.lpop(listName);
        Assert.assertEquals(1, redis.llen(listName));
        
        redis.lpop(listName);
        Assert.assertEquals(0, redis.llen(listName));
        
        redis.lpop(listName);
        Assert.assertEquals(0, redis.llen(listName));
    }
    
    @Test
    public void testLPOP() {
        DryRedisList redis = new DryRedisList();
        
        Assert.assertNull(redis.lpop(listName));
        
        Assert.assertEquals(1, redis.rpush(listName, "value1"));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));

        Assert.assertEquals(1, redis.rpush(listName, "value1"));
        Assert.assertEquals(2, redis.rpush(listName, "value2"));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertEquals("value2", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));
    }
    
    @Test
    public void testLPUSH() {
        DryRedisList redis = new DryRedisList();
        
        Assert.assertNull(redis.lpop(listName));
        
        Assert.assertEquals(1, redis.lpush(listName, "value1"));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));

        Assert.assertEquals(1, redis.lpush(listName, "value1"));
        Assert.assertEquals(2, redis.lpush(listName, "value2"));
        Assert.assertEquals("value2", redis.lpop(listName));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));
    }
    
    @Test
    public void testLPUSHX() {
        DryRedisList redis = new DryRedisList();
        
        Assert.assertNull(redis.lpop(listName));
        
        Assert.assertEquals(0, redis.lpushx(listName, "value1"));
        Assert.assertNull(redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));

        Assert.assertEquals(1, redis.lpush(listName, "value1"));
        Assert.assertEquals(2, redis.lpushx(listName, "value2"));
        Assert.assertEquals("value2", redis.lpop(listName));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));
    }

    @Test
    public void testRPUSH() {
        DryRedisList redis = new DryRedisList();
        
        Assert.assertNull(redis.lpop(listName));
        
        Assert.assertEquals(1, redis.rpush(listName, "value1"));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));

        Assert.assertEquals(1, redis.rpush(listName, "value1"));
        Assert.assertEquals(2, redis.rpush(listName, "value2"));
        Assert.assertEquals("value2", redis.lpop(listName));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));
    }

    @Test
    public void testRPUSHX() {
        DryRedisList redis = new DryRedisList();
        
        Assert.assertNull(redis.lpop(listName));
        
        Assert.assertEquals(0, redis.rpushx(listName, "value1"));
        Assert.assertNull(redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));

        Assert.assertEquals(1, redis.rpush(listName, "value1"));
        Assert.assertEquals(2, redis.rpushx(listName, "value2"));
        Assert.assertEquals("value2", redis.lpop(listName));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));
    }
    
    @Test
    public void testIntegration() {
        DryRedisList list = new DryRedisList();
        
        // check that there are no elements and size is zero
        Assert.assertEquals(0, list.llen(listName));
        Assert.assertNull(list.lpop(listName));
        Assert.assertNull(list.rpop(listName));
        Assert.assertNull(list.lindex(listName, 0));
        Assert.assertNull(list.lindex(listName, 1));
        Assert.assertNull(list.lindex(listName, 10));
        
        // now let's insert elements
        Assert.assertEquals(1, list.rpush(listName, "one"));
        Assert.assertEquals(2, list.rpush(listName, "two"));
        Assert.assertEquals(3, list.rpush(listName, "three"));
        
        Assert.assertEquals(3, list.llen(listName));
    }
    
    @Test
    public void testBlocking() {
        
    }
}
