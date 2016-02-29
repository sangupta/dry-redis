package com.sangupta.dryredis;

import java.util.ArrayList;
import java.util.List;

import com.sangupta.dryredis.cache.impl.DryRedisGeo;
import com.sangupta.dryredis.cache.impl.DryRedisHash;
import com.sangupta.dryredis.cache.impl.DryRedisHyperLogLog;
import com.sangupta.dryredis.cache.impl.DryRedisList;
import com.sangupta.dryredis.cache.impl.DryRedisSet;
import com.sangupta.dryredis.cache.impl.DryRedisString;
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
	
	protected final DryRedisGeo geoCommands = new DryRedisGeo();
	
	protected final DryRedisHash hashCommands = new DryRedisHash();
	
	protected final DryRedisHyperLogLog hyperLogLogCommands = new DryRedisHyperLogLog();
	
	protected final DryRedisList listCommands = new DryRedisList();
	
	protected final DryRedisSet setCommands = new DryRedisSet();
	
	protected final DryRedisString stringCommands = new DryRedisString();
	
	public DryRedisKeys() {
        caches.add(this.geoCommands);
        caches.add(this.hashCommands);
        caches.add(this.hyperLogLogCommands);
        caches.add(this.listCommands);
        caches.add(this.setCommands);
        caches.add(this.geoCommands);
        caches.add(this.stringCommands);
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
	    DryRedisCacheType foundKeyType = keyType(key);
	    if(foundKeyType == null) {
	        return;
	    }
	    
	    if(type != foundKeyType) {
	        throw new IllegalArgumentException("Key contains a different type of data-structure");
	    }
	}
}
