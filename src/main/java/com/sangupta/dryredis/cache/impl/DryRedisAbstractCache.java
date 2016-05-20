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
