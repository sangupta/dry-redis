package com.sangupta.dryredis;

import org.junit.Assert;
import org.junit.Test;

public class TestDryRedis {

    @Test
    public void testDatabases() {
        DryRedis instance = DryRedis.getDatabase();
        
        Assert.assertTrue(instance == DryRedis.getDatabase());
        Assert.assertTrue(instance == DryRedis.getDatabase(null));
        Assert.assertTrue(instance == DryRedis.getDatabase(""));
        Assert.assertTrue(instance == DryRedis.getDatabase(" "));
        
        instance = DryRedis.getDatabase("sangupta");
        Assert.assertTrue(instance == DryRedis.getDatabase("sangupta"));
    }
    
}
