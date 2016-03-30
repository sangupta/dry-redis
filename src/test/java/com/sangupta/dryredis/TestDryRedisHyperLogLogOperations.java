package com.sangupta.dryredis;

import com.sangupta.dryredis.cache.DryRedisHyperLogLogOperations;
import com.sangupta.dryredis.cache.impl.TestDryRedisHyperLogLog;

/**
 * Test {@link DryRedisHyperLogLogOperations} using {@link DryRedis} instance.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisHyperLogLogOperations extends TestDryRedisHyperLogLog {
    
    @Override
    protected DryRedisHyperLogLogOperations getRedis() {
        return DryRedis.getDatabase();
    }

}
