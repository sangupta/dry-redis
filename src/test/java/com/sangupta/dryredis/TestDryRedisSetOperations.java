package com.sangupta.dryredis;

import com.sangupta.dryredis.cache.DryRedisSetOperations;
import com.sangupta.dryredis.cache.impl.TestDryRedisSet;

/**
 * Test {@link DryRedisSetOperations} using {@link DryRedis} instance.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisSetOperations extends TestDryRedisSet {
    
    @Override
    protected DryRedisSetOperations getRedis() {
        DryRedis redis = DryRedis.getDatabase();
        redis.flushdb();
        return redis;
    }

}
