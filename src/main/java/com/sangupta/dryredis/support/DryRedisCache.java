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

    /**
     * Rename the given key to the new key name.
     * 
     * @param key
     * @param newKey
     */
    public void rename(String key, String newKey);

    /**
     * Flush the entire cache for the database.
     * 
     */
    public void flushCache();
    
    public int pexpireat(String key, long epochAsMilliseconds);

}
