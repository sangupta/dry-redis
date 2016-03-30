package com.sangupta.dryredis;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.dryredis.cache.DryRedisGeoOperations;
import com.sangupta.dryredis.cache.impl.TestDryRedisGeo;
import com.sangupta.dryredis.support.DryRedisGeoUnit;

/**
 * Test {@link DryRedisGeoOperations} using {@link DryRedis} instance.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisGeoOperations extends TestDryRedisGeo {
    
    @Test
    public void testGEOADD() {
        DryRedisGeoOperations redis = getRedis();
        
        Assert.assertEquals(1, redis.geoadd("Sicily", 13.361389d, 38.115556d, "Palermo"));
        Assert.assertEquals(1, redis.geoadd("Sicily", 15.087269d, 37.502669d, "Catania"));
    }
    
    @Test
    public void testGEODIST() {
        DryRedisGeoOperations redis = getRedis();
        
        redis.geoadd("Sicily", 13.361389d, 38.115556d, "Palermo");
        redis.geoadd("Sicily", 15.087269d, 37.502669d, "Catania");
        
        Assert.assertEquals(166274.15156960033d, redis.geodist("Sicily", "Palermo", "Catania", DryRedisGeoUnit.Meters), 0.1d);
        
    }
    
    @Test
    public void testGEORADIUS() {
        DryRedisGeoOperations redis = getRedis();
        
        redis.geoadd("Sicily", 13.361389d, 38.115556d, "Palermo");
        redis.geoadd("Sicily", 15.087269d, 37.502669d, "Catania");
        
        Assert.assertTrue(TestUtils.equalUnsorted(TestUtils.asList("Catania"), redis.georadius("Sicily", 15d, 37d, 100, DryRedisGeoUnit.KiloMeters)));
        Assert.assertTrue(TestUtils.equalUnsorted(TestUtils.asList("Catania", "Palermo"), redis.georadius("Sicily", 15d, 37d, 200, DryRedisGeoUnit.KiloMeters)));
    }
    
    @Override
    protected DryRedisGeoOperations getRedis() {
        return DryRedis.getDatabase();
    }

}
