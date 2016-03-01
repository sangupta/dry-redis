package com.sangupta.dryredis;

import com.sangupta.dryredis.cache.DryRedisListOperations;
import com.sangupta.dryredis.cache.impl.TestDryRedisList;

/**
 * Test {@link DryRedisListOperations} using {@link DryRedis} instance.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisListOperations extends TestDryRedisList {
    
    @Override
    protected DryRedisListOperations getRedis() {
        return new DryRedis();
    }

}
