package com.sangupta.dryredis;

import com.sangupta.dryredis.cache.DryRedisSortedSetOperations;
import com.sangupta.dryredis.cache.impl.TestDryRedisSortedSet;

/**
 * Test {@link DryRedisSortedSetOperations} using {@link DryRedis} instance.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisSortedSetOperations extends TestDryRedisSortedSet {

    @Override
    protected DryRedisSortedSetOperations getRedis() {
        return DryRedis.getDatabase();
    }
    
}
