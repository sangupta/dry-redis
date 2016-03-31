package com.sangupta.dryredis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sangupta.dryredis.cache.impl.DryRedisGeo;
import com.sangupta.dryredis.cache.impl.DryRedisHash;
import com.sangupta.dryredis.cache.impl.DryRedisHyperLogLog;
import com.sangupta.dryredis.cache.impl.DryRedisList;
import com.sangupta.dryredis.cache.impl.DryRedisSet;
import com.sangupta.dryredis.cache.impl.DryRedisSortedSet;
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
    
    private final Map<String, Long> EXPIRE_TIMES = new HashMap<String, Long>();
	
	private final List<DryRedisCache> caches = new ArrayList<DryRedisCache>();
	
	protected final DryRedisGeo geoCommands = new DryRedisGeo();
	
	protected final DryRedisHash hashCommands = new DryRedisHash();
	
	protected final DryRedisHyperLogLog hyperLogLogCommands = new DryRedisHyperLogLog();
	
	protected final DryRedisList listCommands = new DryRedisList();
	
	protected final DryRedisSet setCommands = new DryRedisSet();
	
	protected final DryRedisString stringCommands = new DryRedisString();
	
	protected final DryRedisSortedSet sortedSetCommands = new DryRedisSortedSet();
	
	public DryRedisKeys() {
        caches.add(this.geoCommands);
        caches.add(this.hashCommands);
        caches.add(this.hyperLogLogCommands);
        caches.add(this.listCommands);
        caches.add(this.setCommands);
        caches.add(this.geoCommands);
        caches.add(this.stringCommands);
        caches.add(this.sortedSetCommands);
    }
	
	/**
	 * Delete the keys from this redis instance.
	 * 
	 * @param key
	 * @return
	 */
	public int del(String key) {
	    int deleted = 0;
	    for(DryRedisCache cache : caches) {
	        deleted = deleted + cache.del(key);
	    }
	    
	    return deleted;
	}

	/**
	 * Dump the given key into a byte[] array that could later be used
	 * to restore the key back into this redis instance.
	 * 
	 * @param key
	 * @return
	 */
	public byte[] dump(String key) {
	    DryRedisCache cache = this.getCache(key);
	    if(cache == null) {
	        return null;
	    }
	    
	    return cache.dump(key);
	}
	
	public String flushdb() {
	    for(DryRedisCache cache : caches) {
	        cache.flushCache();
	    }
	    
	    return "OK";
	}
	
	/**
	 * Check if a key exists in this redis instance.
	 * 
	 * @param key
	 * @return
	 */
	public int exists(String key) {
	    for(DryRedisCache cache : caches) {
	        if(cache.hasKey(key)) {
	            // check for expiry
	            if(isExpired(key)) {
	                cache.del(key);
	                return 0;
	            }
	            
	            return 1;
	        }
	    }
	    
	    return 0;
	}
	
	/**
	 * Set the expiration time of the key to given number of seconds starting now.
	 * 
	 * @param key
	 * @param seconds
	 * @return
	 */
	public int expire(String key, int seconds) {
	    return this.pexpireat(key, System.currentTimeMillis() + seconds * 1000l);
	}
	
	/**
	 * Set the expiration time of the key to the given epoch time in seconds.
	 * 
	 * @param key
	 * @param epochAsSeconds
	 * @return
	 */
	public int expireat(String key, long epochAsSeconds) {
        return this.pexpireat(key, epochAsSeconds * 1000l);
	}
	
	public int pexpire(String key, long milliseconds) {
	    return this.pexpireat(key, System.currentTimeMillis() + milliseconds);
	}
	
	public int pexpireat(String key, long epochAsMilliseconds) {
	    if(this.exists(key) == 0) {
            return 0;
        }
	    
	    EXPIRE_TIMES.put(key, epochAsMilliseconds);
	    return 1;
	}
	
	public String rename(String key, String newKey) {
	    if(key == null || newKey == null) {
	        return "ERROR";
	    }
	    
	    if(key.equals(newKey)) {
	        return "ERROR";
	    }
	    
	    if(this.exists(key) == 0) {
	        return "ERROR";
	    }
	    
	    DryRedisCache cache = this.getCache(key);
	    cache.rename(key, newKey);
	    return "OK";
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
	
	/**
	 * Find the exact cache in which a given key is present.
	 * 
	 * @param key
	 * @return
	 */
	protected DryRedisCache getCache(String key) {
        for(DryRedisCache cache : caches) {
            if(cache.hasKey(key)) {
                return cache;
            }
        }
        
        return null;
	}
	
	/**
     * Find the cache type for the cache in which the given key is present.
     * 
     * @param key
     * @return
     */
	protected DryRedisCacheType keyType(String key) {
	    DryRedisCache cache = this.getCache(key);
	    if(cache == null) {
	        return null;
	    }
	    
	    return cache.getType();
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

	/**
     * Check if the {@link DryRedisCacheType} for the given key is the same as
     * the provided {@link DryRedisCacheType}. If not, an
     * {@link IllegalArgumentException} is raised.
     * 
     * @param key
     *            the key to check cache type for
     * 
     * @param type
     *            the type to match with
     * 
     * @throws IllegalArgumentException
     *             if there is a mismatch in cache type
     */
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
