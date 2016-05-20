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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.dryredis.support.DryRedisInsertOrder;

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
        DryRedisListOperations redis = getRedis();
        
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
        DryRedisListOperations redis = getRedis();
        
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
        DryRedisListOperations redis = getRedis();
        
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
        DryRedisListOperations redis = getRedis();
        
        Assert.assertNull(redis.lpop(listName));
        
        Assert.assertEquals(1, redis.lpush(listName, "value1"));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));

        Assert.assertEquals(1, redis.lpush(listName, "value1"));
        Assert.assertEquals(2, redis.lpush(listName, "value2"));
        Assert.assertEquals("value2", redis.lpop(listName));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));
        
        // multi-value
        Assert.assertEquals(0, redis.lpush("key", TestUtils.asList((String[]) null)));
        
        List<String> list = TestUtils.asList("value1", "value2", "value3", "value4", "value5");
        List<String> reverse = new ArrayList<String>(list);
        Collections.reverse(reverse);
        Assert.assertEquals(5, redis.lpush("key", list));
        Assert.assertEquals(reverse, redis.lrange("key", 0, 10));
        Assert.assertEquals(10, redis.lpush("key", list));
    }
    
    @Test
    public void testLPUSHX() {
        DryRedisListOperations redis = getRedis();
        
        Assert.assertNull(redis.lpop(listName));
        
        Assert.assertEquals(-1, redis.lpushx(listName, "value1"));
        Assert.assertNull(redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));

        Assert.assertEquals(1, redis.lpush(listName, "value1"));
        Assert.assertEquals(2, redis.lpushx(listName, "value2"));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertEquals("value2", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));
        
        // multi-value
        Assert.assertEquals(-1, redis.lpushx("key", TestUtils.asList((String[]) null)));
        Assert.assertEquals(-1, redis.lpushx("key", TestUtils.asList("value1", "value2", "value3", "value4", "value5")));
        redis.lpush("key", "values");
        Assert.assertEquals(6, redis.lpushx("key", TestUtils.asList("value1", "value2", "value3", "value4", "value5")));
    }
    
    @Test
    public void testLRANGE() {
        DryRedisListOperations redis = getRedis();
        
        Assert.assertNull(redis.lrange("non-existent", 0, 1));
        
        redis.lpush("key", "value");
        redis.lpop("key");
        Assert.assertNotNull(redis.lrange("key", 0, 1));
        Assert.assertTrue(redis.lrange("key", 0, 1).isEmpty());
        
        redis.lpush("key", TestUtils.asList("value1", "value2", "value3", "value4", "value5"));
        Assert.assertTrue(TestUtils.equalUnsorted(TestUtils.asList("value4", "value3"), redis.lrange("key", 1, 2)));
        Assert.assertTrue(TestUtils.equalUnsorted(TestUtils.asList("value5"), redis.lrange("key", 0, 0)));
        Assert.assertTrue(TestUtils.equalUnsorted(TestUtils.asList("value2", "value1"), redis.lrange("key", -2, -1)));
        Assert.assertTrue(redis.lrange("key", -1, -2).isEmpty());
    }

    @Test
    public void testRPUSH() {
        DryRedisListOperations redis = getRedis();
        
        Assert.assertNull(redis.lpop(listName));
        
        Assert.assertEquals(1, redis.rpush(listName, "value1"));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));

        Assert.assertEquals(1, redis.rpush(listName, "value1"));
        Assert.assertEquals(2, redis.rpush(listName, "value2"));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertEquals("value2", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));
        
        // multi-add
        List<String> list = TestUtils.asList("v1", "v2", "v3", "v4", "v5");
        Assert.assertEquals(5, redis.rpush("next", list));
        Assert.assertEquals(list, redis.lrange("next", 0, 10));
    }

    @Test
    public void testRPUSHX() {
        DryRedisListOperations redis = getRedis();
        
        Assert.assertNull(redis.lpop(listName));
        
        Assert.assertEquals(0, redis.rpushx(listName, "value1"));
        Assert.assertNull(redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));

        Assert.assertEquals(1, redis.rpush(listName, "value1"));
        Assert.assertEquals(2, redis.rpushx(listName, "value2"));
        Assert.assertEquals("value1", redis.lpop(listName));
        Assert.assertEquals("value2", redis.lpop(listName));
        Assert.assertNull(redis.lpop(listName));
    }
    
    @Test
    public void testLREM() {
        DryRedisListOperations list = getRedis();
        
        Assert.assertEquals(0, list.lrem("non-existent", 5, "v"));
        
        list.rpush("empty", "value");
        list.rpop("empty");
        Assert.assertEquals(0, list.lrem("empty", 5, "v"));
        
        list.rpush("key", "v1");
        list.rpush("key", "v2");
        list.rpush("key", "v3");
        list.rpush("key", "v1");
        list.rpush("key", "v2");
        list.rpush("key", "v3");
        list.rpush("key", "v1");
        list.rpush("key", "v2");
        list.rpush("key", "v3");
        list.rpush("key", "v1");
        list.rpush("key", "v2");
        
        Assert.assertEquals(1, list.lrem("key", -1, "v2"));
        Assert.assertEquals(TestUtils.asList("v1", "v2", "v3", "v1", "v2", "v3", "v1", "v2", "v3", "v1"), list.lrange("key", 0, 20));
        Assert.assertEquals(2, list.lrem("key", -2, "v2"));
        Assert.assertEquals(TestUtils.asList("v1", "v2", "v3", "v1", "v3", "v1", "v3", "v1"), list.lrange("key", 0, 20));
        Assert.assertEquals(2, list.lrem("key", 2, "v3"));
        Assert.assertEquals(TestUtils.asList("v1", "v2", "v1", "v1", "v3", "v1"), list.lrange("key", 0, 20));
        Assert.assertEquals(4, list.lrem("key", 0, "v1"));
        Assert.assertEquals(TestUtils.asList("v2", "v3"), list.lrange("key", 0, 20));
    }
    
    @Test
    public void testLSET() {
        DryRedisListOperations list = getRedis();
        
        list.rpush("key", "v1");
        list.rpush("key", "v3");

        Assert.assertEquals("OK", list.lset("key", 1, "v2"));
        Assert.assertEquals(TestUtils.asList("v1", "v2"), list.lrange("key", 0, 20));
    }
    
    @Test
    public void testRPOPLPUSH() {
        DryRedisListOperations list = getRedis();
        
        list.rpush("key", "v1");
        list.rpush("key", "v2");
        list.rpush("key", "v3");
        
        list.rpush("next", "n1");
        
        Assert.assertEquals(TestUtils.asList("v1", "v2", "v3"), list.lrange("key", 0, 20));
        Assert.assertEquals("v3", list.rpoplpush("key", "next"));
        Assert.assertEquals(TestUtils.asList("v1", "v2"), list.lrange("key", 0, 20));
        Assert.assertEquals(TestUtils.asList("v3", "n1"), list.lrange("next", 0, 20));
    }
    
    @Test
    public void testRPOP() {
        DryRedisListOperations redis = getRedis();
        
        Assert.assertNull(redis.rpop("non-existent"));
        
        redis.rpush("key", "value");
        Assert.assertEquals("value", redis.rpop("key"));
        
        Assert.assertNull(redis.rpop("key"));
    }
    
    @Test
    public void testLTRIM() {
        DryRedisListOperations redis = getRedis();
        
        List<String> list = TestUtils.asList("value1", "value2", "value3", "value4", "value5");
        redis.rpush("key", list);
        
        Assert.assertEquals("OK", redis.ltrim("key", 1, 3));
        Assert.assertEquals(TestUtils.asList("value2", "value3", "value4"), redis.lrange("key", 0, 20));
        Assert.assertEquals("OK", redis.ltrim("key", 0, 10));
        Assert.assertEquals(TestUtils.asList("value2", "value3", "value4"), redis.lrange("key", 0, 20));
        
        redis.rpush("next", list);
        Assert.assertEquals("OK", redis.ltrim("next", -3, -2));
        Assert.assertEquals(TestUtils.asList("value3", "value4"), redis.lrange("next", 0, 20));

        redis.rpush("tp", list);
        Assert.assertEquals("OK", redis.ltrim("tp", -3, -4));
        Assert.assertTrue(redis.lrange("tp", 0, 20).isEmpty());
        
        Assert.assertEquals("OK", redis.ltrim("non-existent", 0, 10));
        
        redis.rpush("empty", list);
        redis.rpop("empty");
        Assert.assertEquals("OK", redis.ltrim("empty", 0, 10));
        
        // key deleted
        redis.rpush("some", list);
        Assert.assertEquals("OK", redis.ltrim("some", 10, 15));
        Assert.assertNull(redis.lrange("some", 0, 10));
    }
    
    @Test
    public void testLINSERT() {
        DryRedisListOperations redis = getRedis();
        
        // non-existent list
        Assert.assertEquals(0, redis.linsert("non-existent", DryRedisInsertOrder.BEFORE, "4", "3"));
        Assert.assertEquals(0, redis.llen("non-existent"));
        
        // empty list
        redis.rpush("empty", "1");
        redis.rpop("empty");
        Assert.assertEquals(0, redis.linsert("empty", DryRedisInsertOrder.BEFORE, "4", "3"));
        Assert.assertEquals(0, redis.llen("empty"));
        
        // insert before
        redis.rpush("key", "1");
        redis.rpush("key", "2");
        redis.rpush("key", "4");
        Assert.assertEquals(3, redis.llen("key"));
        Assert.assertEquals(4, redis.linsert("key", DryRedisInsertOrder.BEFORE, "4", "3"));
        Assert.assertEquals(4, redis.llen("key"));
        Assert.assertEquals(TestUtils.asList("1", "2", "3", "4"), redis.lrange("key", 0, 10));
        
        // insert after
        Assert.assertEquals(5, redis.linsert("key", DryRedisInsertOrder.AFTER, "3", "5"));
        Assert.assertEquals(5, redis.llen("key"));
        Assert.assertEquals(TestUtils.asList("1", "2", "3", "5", "4"), redis.lrange("key", 0, 10));
        
        // before/after non-existent pivot
        Assert.assertEquals(-1, redis.linsert("key", DryRedisInsertOrder.BEFORE, "7", "9"));
        Assert.assertEquals(-1, redis.linsert("key", DryRedisInsertOrder.AFTER, "7", "9"));
    }
    
    @Test
    public void testIntegration() {
        DryRedisListOperations list = getRedis();
        
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
    
    protected DryRedisListOperations getRedis() {
        return new DryRedisList();
    }
    
}
