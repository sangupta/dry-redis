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

package com.sangupta.dryredis.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.dryredis.cache.DryRedisStringOperations;

public class TestDryRedisString {

    @Test
    public void testAPPEND() {
        DryRedisStringOperations str = getRedis();
        
        Assert.assertEquals(5, str.append("key", "value"));
        Assert.assertEquals(10, str.append("key", "value"));
    }
    
    @Test
    public void testINCR() {
        DryRedisStringOperations str = getRedis();
        
        // no key
        Assert.assertEquals(1, str.incr("test"));
        Assert.assertEquals(2, str.incr("test"));
        Assert.assertEquals(3, str.incr("test"));
        
        // previous key
        str.set("test", "100");
        Assert.assertEquals(101, str.incr("test"));
    }
    
    @Test
    public void testINCRBY() {
        DryRedisStringOperations str = getRedis();
        
        // no key
        Assert.assertEquals(5, str.incrby("test", 5));
        Assert.assertEquals(10, str.incrby("test", 5));
        Assert.assertEquals(15, str.incrby("test", 5));
        
        // previous key
        str.set("test", "100");
        Assert.assertEquals(110, str.incrby("test", 10));
        Assert.assertEquals(100, str.incrby("test", -10));
    }
    
    @Test
    public void testINCRBYFLOAT() {
        DryRedisStringOperations str = getRedis();
        
        // no key
        Assert.assertEquals(5.1d, str.incrbyfloat("test", 5.1d), 0.0001d);
        Assert.assertEquals(10.2d, str.incrbyfloat("test", 5.1d), 0.0001d);
        Assert.assertEquals(15.3d, str.incrbyfloat("test", 5.1d), 0.0001d);
        
        // previous key
        str.set("test", "100.3");
        Assert.assertEquals(105.4d, str.incrbyfloat("test", 5.1d), 0.0001d);
        Assert.assertEquals(110.5d, str.incrbyfloat("test", 5.1d), 0.0001d);
    }
    
    @Test
    public void testDECR() {
        DryRedisStringOperations str = getRedis();
        
        // no key
        Assert.assertEquals(-1, str.decr("test"));
        Assert.assertEquals(-2, str.decr("test"));
        Assert.assertEquals(-3, str.decr("test"));
        
        // previous key
        str.set("test", "100");
        Assert.assertEquals(99, str.decr("test"));
    }

    @Test
    public void testDECRBY() {
        DryRedisStringOperations str = getRedis();
        
        // no key
        Assert.assertEquals(-5, str.decrby("test", 5));
        Assert.assertEquals(-10, str.decrby("test", 5));
        Assert.assertEquals(-15, str.decrby("test", 5));
        
        // previous key
        str.set("test", "100");
        Assert.assertEquals(90, str.decrby("test", 10));
        Assert.assertEquals(100, str.decrby("test", -10));
    }
    
    @Test
    public void testMGET() {
        DryRedisStringOperations str = getRedis();
        
        // string[] version
        Assert.assertNull(str.mget((String[]) null));
        Assert.assertNull(str.mget(new String[]  { }));
        Assert.assertTrue(assertListNullOfSize(str.mget(new String[] { "test" }), 1));
        Assert.assertTrue(assertListNullOfSize(str.mget(new String[] { "test1", "test2" }), 2));
        
        Collection<String> coll = null;
        Assert.assertNull(str.mget(coll));
        
        coll = new ArrayList<String>();
        Assert.assertNull(str.mget(coll));
        
        coll.add("test1");
        Assert.assertTrue(assertListNullOfSize(str.mget(coll), 1));
        
        coll.add("test2");
        Assert.assertTrue(assertListNullOfSize(str.mget(coll), 2));
    }
    
    @Test
    public void testSETandGET() {
        DryRedisStringOperations str = getRedis();
        
        Assert.assertNull(str.get("key"));
        Assert.assertEquals("OK", str.set("key", "value"));
        Assert.assertNotNull(str.get("key"));
        Assert.assertEquals("value", str.get("key"));
    }
    
    @Test
    public void testSETNX() {
        DryRedisStringOperations str = getRedis();
        
        Assert.assertEquals("OK", str.setnx("key", "value"));
        Assert.assertNull(str.setnx("key", "value"));
    }
    
    @Test
    public void testSETXX() {
        DryRedisStringOperations str = getRedis();
        
        Assert.assertNull(str.setxx("key", "value"));
        Assert.assertEquals("OK", str.set("key", "value"));
        Assert.assertEquals("OK", str.setxx("key", "value2"));
        Assert.assertEquals("value2", str.get("key"));
    }
    
    @Test
    public void testGETRANGE() {
        DryRedisStringOperations str = getRedis();
        
        Assert.assertEquals("OK", str.set("key", "values"));
        Assert.assertEquals("alu", str.getrange("key", 1, 3));
        Assert.assertEquals("ues", str.getrange("key", -3, -1));
        Assert.assertEquals("", str.getrange("key", -1, -3));
        Assert.assertEquals("", str.getrange("non-existent", 1, 3));
        
        str.set("hello", "world");
        Assert.assertEquals("", str.getrange("hello", 7, 9));
        Assert.assertEquals("", str.getrange("hello", 16, 9));
    }
    
    @Test
    public void testSETRANGE() {
        DryRedisStringOperations str = getRedis();
        
        Assert.assertEquals("OK", str.set("key", "values"));
        Assert.assertEquals(6, str.setrange("key", 1, "tip"));
        Assert.assertEquals("vtipes", str.get("key"));

        Assert.assertEquals(7, str.setrange("key", 4, "nim"));
        Assert.assertEquals("vtipnim", str.get("key"));

        try {
            str.setrange("somekey", -10, "hello");
            Assert.assertTrue(false);
        } catch(IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        
        
        Assert.assertEquals(7, str.setrange("hello", 3, "test"));
        char[] array = { 0, 0, 0, 't', 'e', 's', 't' };
        Assert.assertEquals(new String(array), str.get("hello"));
    }
    
    @Test
    public void testBITCOUNT() {
        DryRedisStringOperations str = getRedis();
        
        str.set("test", "hello");
        Assert.assertEquals(21, str.bitcount("test"));
        Assert.assertEquals(11, str.bitcount("test", 0, 2));
        Assert.assertEquals(8, str.bitcount("test", 1, 2));
        Assert.assertEquals(0, str.bitcount("test", -1, -2));
        Assert.assertEquals(10, str.bitcount("test", -2, -1));
        
        Assert.assertEquals(0, str.bitcount("non-existent"));
    }
    
    @Test
    public void testSTRLEN() {
        DryRedisStringOperations str = getRedis();
        
        Assert.assertEquals(0, str.strlen("non-existent"));
        str.set("test", "hello");
        Assert.assertEquals(5, str.strlen("test"));
        str.set("test", "hello world");
        Assert.assertEquals(11, str.strlen("test"));
    }
    
    @Test
    public void testGETSET() {
        DryRedisStringOperations str = getRedis();
        
        Assert.assertNull(str.getset("key", "value"));
        Assert.assertEquals("value", str.getset("key", "value2"));
        Assert.assertEquals("value2", str.getset("key", "value3"));
    }
    
    @Test
    public void testDEL() {
        DryRedisStringOperations str = getRedis();

        // TODO: fix these tests
        
//        Assert.assertFalse(str.hasKey("hello"));
//        str.set("hello", "world");
//        Assert.assertTrue(str.hasKey("hello"));
//        Assert.assertEquals(1, str.del("hello"));
//        Assert.assertFalse(str.hasKey("hello"));
    }
    
    // private methods

    private boolean assertListNullOfSize(List<String> list, int size) {
        if(size == 0) {
            if(list == null || list.isEmpty()) {
                return true;
            }
            
            return false;
        }
        
        if(list.size() != size) {
            return false;
        }
        
        for(String str : list) {
            if(str != null) {
                return false;
            }
        }
        
        return true;
    }
    
    protected DryRedisStringOperations getRedis() {
        return new DryRedisString();
    }
}
