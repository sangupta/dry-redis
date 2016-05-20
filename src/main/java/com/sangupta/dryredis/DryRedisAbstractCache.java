/**
 *
 * dry-redis: In-memory pure java implementation to Redis
 * Copyright (c) 2016, Sandeep Gupta
 * 
 * http://sangupta.com/projects/dry-redis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.sangupta.dryredis;

import java.util.HashMap;
import java.util.HashSet;
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

    // TODO: change this into guava cache - so that we can clean up old timers
    private final Map<String, Long> EXPIRE_TIMES = new HashMap<String, Long>();
    
    public int pexpireat(String key, long epochAsMilliseconds) {
        if(!this.hasKey(key)) {
            return 0;
        }
        
        EXPIRE_TIMES.put(key, epochAsMilliseconds);
        return 1;
    }
    
    /**
     * Check if a key has expired or not.
     * 
     * @param key
     * @return
     */
    protected boolean isExpired(String key) {
        Long expiry = EXPIRE_TIMES.get(key);
        if(expiry == null) {
            return false;
        }
        
        long value = expiry.longValue();
        if(System.currentTimeMillis() >= value) {
            return true;
        }
        
        return false;
    }

    @Override
    public int del(String keyPattern) {
        if(keyPattern.contains("*") || keyPattern.contains("?")) {
            // wildcard-match
            Set<String> keySet = this.store.keySet();
            if(keySet == null || keySet.isEmpty()) {
                return 0;
            }
            
            Set<String> toRemove = new HashSet<String>();
            for(String key : keySet) {
                if(DryRedisUtils.wildcardMatch(key, keyPattern)) {
                    toRemove.add(key);
                }
            }
            
            if(toRemove.isEmpty()) {
                return 0;
            }

            for(String key : toRemove) {
                this.store.remove(key);
            }
            
            return toRemove.size();
        }
        
        // normal key
        T removed = this.store.remove(keyPattern);
        if(removed == null) {
            return 0;
        }
        
        return 1;
    }

    @Override
    public boolean hasKey(String key) {
        if(!this.store.containsKey(key)) {
            return false;
        }

        // check for expiry
        if(isExpired(key)) {
            this.del(key);
            return false;
        }
        
        return true;
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
