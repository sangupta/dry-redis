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
abstract class DryRedisKeys {
    
    /**
     * A list of all {@link DryRedisCache} implementations that reflect a
     * category of redis commands.
     */
	private final List<DryRedisCache> caches = new ArrayList<DryRedisCache>();
	
	/**
	 * Redis GEO commands implementation
	 */
	protected final DryRedisGeo geoCommands = new DryRedisGeo();
	
	/**
	 * Redis HASH commands implementation
	 */
	protected final DryRedisHash hashCommands = new DryRedisHash();
	
	/**
	 * Redis HyperLogLog commands implementation
	 */
	protected final DryRedisHyperLogLog hyperLogLogCommands = new DryRedisHyperLogLog();
	
	/**
	 * Redis LIST commands implementation
	 */
	protected final DryRedisList listCommands = new DryRedisList();
	
	/**
	 * Redis SET commands implementation
	 */
	protected final DryRedisSet setCommands = new DryRedisSet();
	
	/**
	 * Redis STRING commands implementation
	 */
	protected final DryRedisString stringCommands = new DryRedisString();
	
	/**
	 * Redis SORTED SET commands implementation
	 */
	protected final DryRedisSortedSet sortedSetCommands = new DryRedisSortedSet();
	
	/**
	 * Constructor
	 */
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
	 *            the key pattern to remove
	 * 
	 * @return the total number of keys removed
	 */
	public int del(String key) {
	    int deleted = 0;
	    for(DryRedisCache cache : caches) {
	        deleted = deleted + cache.del(key);
	    }
	    
	    return deleted;
	}

	/**
	 * Dump the given key into a byte[] array that could later be used to
	 * restore the key back into this redis instance.
	 * 
	 * @param key
	 *            the key that needs to be dumped in to a byte-array
	 * 
	 * @return the dumped byte-array, or <code>null</code> if no such key exists
	 */
	public byte[] dump(String key) {
	    DryRedisCache cache = this.getCache(key);
	    if(cache == null) {
	        return null;
	    }
	    
	    return cache.dump(key);
	}
	
	/**
	 * Flush the database by clearing up all keys.
	 * 
	 * @return "OK" at the end
	 */
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
	 *            the key to look for
	 * 
	 * @return <code>1</code> if key is found, <code>0</code> otherwise
	 */
	public int exists(String key) {
	    for(DryRedisCache cache : caches) {
	        if(cache.hasKey(key)) {
	            return 1;
	        }
	    }
	    
	    return 0;
	}
	
	/**
	 * Set the expiration time of the key to given number of seconds starting
	 * now.
	 * 
	 * @param key
	 *            the key for which expiration is to be set
	 * 
	 * @param seconds
	 *            the number of seconds to expiration
	 * 
	 * @return <code>0</code> if we could not set expire, or key was not found.
	 *         <code>1</code> if expiration was set
	 */
	public int expire(String key, int seconds) {
	    return this.pexpireat(key, System.currentTimeMillis() + seconds * 1000l);
	}
	
	/**
	 * Set the expiration time of the key to the given epoch time in seconds.
	 * 
	 * @param key
	 *            the key for which expiration is to be set
	 * 
	 * @param epochAsSeconds
	 *            the epoch millis for expiration
	 * 
	 * @return <code>0</code> if we could not set expire, or key was not found.
	 *         <code>1</code> if expiration was set
	 */
	public int expireat(String key, long epochAsSeconds) {
        return this.pexpireat(key, epochAsSeconds * 1000l);
	}
	
	public int pexpire(String key, long milliseconds) {
	    return this.pexpireat(key, System.currentTimeMillis() + milliseconds);
	}
	
	public int pexpireat(String key, long epochAsMilliseconds) {
	    for(DryRedisCache cache : this.caches) {
	        int result = cache.pexpireat(key, epochAsMilliseconds);
	        if(result > 0) {
	            return result;
	        }
	    }
	    
	    return 0;
	}
	
	/**
	 * Rename key provided to a new name.
	 * 
	 * @param key
	 *            the key name as of now
	 * 
	 * @param newKey
	 *            the destination key name to use
	 * 
	 * @return "OK" if renaming succeeds, "ERROR" otherwise
	 */
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
	
	public int renamenx(String key, String newKey) {
	    if(key == null || newKey == null) {
            return 0;
        }
        
        if(key.equals(newKey)) {
            return 0;
        }
        
        if(this.exists(key) == 0) {
            return 0;
        }
        
        if(this.exists(newKey) == 1) {
            return 0;
        }
        
        DryRedisCache cache = this.getCache(key);
        cache.rename(key, newKey);
        return 1;
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
	
	public String migrate(String database, String key) {
	    throw new RuntimeException("not yet implemented");
	}
	
	public String migrate(String database, boolean copy, boolean replace, String key) {
	    throw new RuntimeException("not yet implemented");
	}
	
    public String migrate(String database, boolean copy, boolean replace, String... key) {
        throw new RuntimeException("not yet implemented");
    }
    
    public int move(String key, String database) {
        throw new RuntimeException("not yet implemented");
    }
    
    public Object object(String subCommand, String... arguments) {
        throw new RuntimeException("not yet implemented");
    }
    
    public int persist(String key) {
        throw new RuntimeException("not yet implemented");
    }
    
    public Object randomkey() {
        throw new RuntimeException("not yet implemented");
    }
    
    public String restore(String key, byte[] data) {
        throw new RuntimeException("not yet implemented");
    }
    
    public void sort(String key) {
        throw new RuntimeException("not yet implemented");
    }
    
    public long ttl(String key) {
        throw new RuntimeException("not yet implemented");
    }
    
    public long pttl(String key) {
        throw new RuntimeException("not yet implemented");
    }
    
    /**
	 * Being an in-memory and single-machine instance of Redis - there are no
	 * slaves for us. Thus, there is nothing to do in the WAIT command. Thus we
	 * return <code>0</code> to signify that we updated zero slaves.
	 * 
	 * @param numSlaves
	 *            the number-of-slaves
	 * 
	 * @param timeOutInMilliseconds
	 *            the time out value in milliseconds
	 * 
	 * @return the number of slaves that were updated
	 */
    public int wait(int numSlaves, long timeOutInMilliseconds) {
        return 0; 
    }
    
	// private methods
	
	/**
	 * Find the exact cache in which a given key is present.
	 * 
	 * @param key
	 *            the key we are looking for
	 * 
	 * @return the {@link DryRedisCache} where key is present, <code>null</code>
	 *         otherwise
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
	 *            the key we are looking for
	 * 
	 * @return the {@link DryRedisCacheType} where key is present,
	 *         <code>null</code> otherwise
	 */
	protected DryRedisCacheType keyType(String key) {
	    DryRedisCache cache = this.getCache(key);
	    if(cache == null) {
	        return null;
	    }
	    
	    return cache.getType();
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
