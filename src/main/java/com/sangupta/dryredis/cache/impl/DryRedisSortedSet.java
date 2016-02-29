package com.sangupta.dryredis.cache.impl;

import java.util.List;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;

public class DryRedisSortedSet implements DryRedisCache {

    @Override
    public int del(String key) {
        return 0;
    }

    @Override
    public DryRedisCacheType getType() {
        return DryRedisCacheType.SORTED_SET;
    }

    @Override
    public boolean hasKey(String key) {
        return false;
    }

    @Override
    public void keys(String pattern, List<String> keys) {
        
    }
}
