package com.sangupta.dryredis.cache.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisUtils;

/**
 * Abstract class that makes it easier to build {@link DryRedisCache} implementations
 * by providing some common functionality.
 * 
 * @author sangupta
 *
 * @param <T>
 */
abstract class DryRedisAbstractCache<T> implements DryRedisCache {
    
    protected final Map<String, T> store = new HashMap<String, T>();

    @Override
    public int del(String key) {
        T removed = this.store.remove(key);
        if(removed == null) {
            return 0;
        }
        
        return 1;
    }

    @Override
    public boolean hasKey(String key) {
        return this.store.containsKey(key);
    }
    
    @Override
    public void keys(String pattern, List<String> keys) {
        Set<String> keySet = this.store.keySet();
        if(keySet == null || keySet.isEmpty()) {
            return;
        }
        
        for(String key : keySet) {
            if(DryRedisUtils.wildcardMatch(key, pattern)) {
                keys.add(key);
            }
        }
    }
    
    @Override
    public void flushCache() {
        this.store.clear();
    }
    
    @Override
    public byte[] dump(String key) {
        return DryRedisUtils.createDump(this.getType(), key, this.store.get(key));
    }

    @Override
    public void rename(String key, String newKey) {
        T value = this.store.remove(key);
        this.store.put(newKey, value);
    }
    
}
