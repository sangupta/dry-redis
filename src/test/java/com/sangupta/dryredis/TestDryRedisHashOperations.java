package com.sangupta.dryredis;

import com.sangupta.dryredis.cache.DryRedisHashOperations;
import com.sangupta.dryredis.cache.impl.TestDryRedisHash;

/**
 * Test {@link DryRedisHashOperations} using {@link DryRedis} instance.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisHashOperations extends TestDryRedisHash {

    @Override
    protected DryRedisHashOperations getRedis() {
        DryRedis redis = DryRedis.getDatabase();
        redis.flushdb();
        return redis;
    }
    
}
