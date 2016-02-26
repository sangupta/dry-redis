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
    public void testRPUSHX() {
        DryRedisList redis = new DryRedisList();
        
        Assert.assertEquals(0, redis.rpushx(listName, "value"));
        Assert.assertEquals(0, redis.llen(listName));
        
        Assert.assertEquals(1, redis.lpush(listName, "value1"));
        Assert.assertEquals(1, redis.llen(listName));
        Assert.assertEquals("value1", redis.lindex(listName, -1));
        Assert.assertEquals(2, redis.rpushx(listName, "value2"));
        Assert.assertEquals(2, redis.llen(listName));
        Assert.assertEquals("value2", redis.lindex(listName, -1));
        Assert.assertEquals(3, redis.rpushx(listName, "value3"));
        Assert.assertEquals(3, redis.llen(listName));
        Assert.assertEquals("value3", redis.lindex(listName, -1));
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
        list.rpush(listName, "one");
        list.rpush(listName, "two");
        list.rpush(listName, "three");
        
        Assert.assertEquals(3, list.llen(listName));
    }
    
    @Test
    public void testBlocking() {
        
    }
}
