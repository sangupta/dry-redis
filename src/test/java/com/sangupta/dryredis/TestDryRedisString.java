package com.sangupta.dryredis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestDryRedisString {

    @Test
    public void testAPPEND() {
        DryRedisString str = new DryRedisString();
        
        Assert.assertEquals(5, str.append("key", "value"));
        Assert.assertEquals(10, str.append("key", "value"));
    }
    
    @Test
    public void testINCR() {
        DryRedisString str = new DryRedisString();
        
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
        DryRedisString str = new DryRedisString();
        
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
        DryRedisString str = new DryRedisString();
        
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
        DryRedisString str = new DryRedisString();
        
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
        DryRedisString str = new DryRedisString();
        
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
        DryRedisString str = new DryRedisString();
        
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
        DryRedisString str = new DryRedisString();
        
        Assert.assertNull(str.get("key"));
        Assert.assertEquals("OK", str.set("key", "value"));
        Assert.assertNotNull(str.get("key"));
        Assert.assertEquals("value", str.get("key"));
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
}
