package com.sangupta.dryredis;

import com.sangupta.dryredis.cache.DryRedisGeoOperations;
import com.sangupta.dryredis.cache.impl.TestDryRedisGeo;

/**
 * Test {@link DryRedisGeoOperations} using {@link DryRedis} instance.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisGeoOperations extends TestDryRedisGeo {
    
    @Override
    protected DryRedisGeoOperations getRedis() {
        return new DryRedis();
    }

}
