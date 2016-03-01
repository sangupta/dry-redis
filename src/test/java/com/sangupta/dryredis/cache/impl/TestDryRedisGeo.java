package com.sangupta.dryredis.cache.impl;

import org.junit.Test;

import com.sangupta.dryredis.cache.DryRedisGeoOperations;

/**
 * Unit tests for {@link DryRedisGeo}.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisGeo {

    @Test
    public void test() {
        
    }
    
    protected DryRedisGeoOperations getRedis() {
        return new DryRedisGeo();
    }
    
}
