package com.sangupta.dryredis.cache.impl;

import org.junit.Test;

import com.sangupta.dryredis.cache.DryRedisHyperLogLogOperations;

/**
 * Unit tests for {@link DryRedisHyperLogLog}.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisHyperLogLog {

    @Test
    public void test() {
        
    }
    
    protected DryRedisHyperLogLogOperations getRedis() {
        return new DryRedisHyperLogLog();
    }
    
}
