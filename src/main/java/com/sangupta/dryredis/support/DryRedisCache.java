package com.sangupta.dryredis.support;

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

}
