package com.sangupta.dryredis;

import com.sangupta.dryredis.cache.DryRedisStringOperations;
import com.sangupta.dryredis.cache.impl.TestDryRedisString;

/**
 * Test {@link DryRedisStringOperations} using {@link DryRedis} instance.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisStringOperations extends TestDryRedisString {

    @Override
    protected DryRedisStringOperations getRedis() {
        return new DryRedis();
    }
    
}
