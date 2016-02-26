package com.sangupta.dryredis;

import java.util.ArrayList;
import java.util.List;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;

/**
 * Implements the methods that are required to work with keys.
 * 
 * This is also the base class for the {@link DryRedis} class that serves
 * as the main point to use this library.
 * 
 * @author sangupta
 *
 */
public abstract class DryRedisKeys {
	
	private final List<DryRedisCache> caches = new ArrayList<DryRedisCache>();
	
	protected final DryRedisGeo geo = new DryRedisGeo();
	
	protected final DryRedisHash hash = new DryRedisHash();
	
	protected final DryRedisHyperLogLog hyperLogLog = new DryRedisHyperLogLog();
	
	protected final DryRedisList list = new DryRedisList();
	
	protected final DryRedisSet set = new DryRedisSet();
	
	protected final DryRedisString redisString = new DryRedisString();
	
	public DryRedisKeys() {
        caches.add(this.geo);
        caches.add(this.hash);
        caches.add(this.hyperLogLog);
        caches.add(this.list);
        caches.add(this.set);
        caches.add(this.geo);
        caches.add(this.redisString);
    }
	
	public int del(String key) {
	    int deleted = 0;
	    for(DryRedisCache cache : caches) {
	        deleted = deleted + cache.del(key);
	    }
	    
	    return deleted;
	}
	
	public int exists(String key) {
	    for(DryRedisCache cache : caches) {
	        if(cache.hasKey(key)) {
	            return 1;
	        }
	    }
	    
	    return 0;
	}
	
	public List<String> keys(String pattern) {
	    List<String> keys = new ArrayList<String>();
	    
	    for(DryRedisCache cache : caches) {
	        cache.keys(pattern, keys);
	    }
	    
	    return keys;
	}
	
	public String type(String key) {
	    DryRedisCacheType type = keyType(key);
	    if(type == null) {
	        return null;
	    }
	    
	    return type.toString().toLowerCase();
	}
	
	// private methods
	
	protected DryRedisCacheType keyType(String key) {
	    for(DryRedisCache cache : caches) {
	        if(cache.hasKey(key)) {
	            return cache.getType();
	        }
	    }
	    
	    return null;
	}

	protected void matchKeyType(String key, DryRedisCacheType type) {
	    if(type != keyType(key)) {
	        throw new IllegalArgumentException("Key contains a different type of data-structure");
	    }
	}
}
