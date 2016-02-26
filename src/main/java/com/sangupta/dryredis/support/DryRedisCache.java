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
	
	public int del(String key);
	
	public DryRedisCacheType getType();

    public boolean hasKey(String key);

    public void keys(String pattern, List<String> keys);

}
