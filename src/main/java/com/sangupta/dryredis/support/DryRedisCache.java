package com.sangupta.dryredis.support;

import java.util.List;

/**
 * Contract for a class that implements a group of commands over a
 * common subtype.
 * 
 * @author sangupta
 *
 */
public interface DryRedisCache {
	
    /**
     * Delete the keys matching the pattern and return the number of keys
     * removed.
     * 
     * @param key
     * @return
     */
	public int del(String key);
	
	/**
	 * Get the {@link DryRedisCacheType} for this cache.
	 * 
	 * @return
	 */
	public DryRedisCacheType getType();

	/**
	 * Check if a key is present in this cache or not.
	 * 
	 * @param key
	 * @return
	 */
    public boolean hasKey(String key);

    public void keys(String pattern, List<String> keys);

    /**
     * Dump the value stored against the key so that it can be later restored
     * using the RESTORE command.
     * 
     * @param key
     * @return
     */
    public byte[] dump(String key);

}
