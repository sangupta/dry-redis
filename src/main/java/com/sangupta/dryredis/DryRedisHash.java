package com.sangupta.dryredis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;

/**
 * Hash operations from Redis
 * 
 * @author sangupta
 *
 * @param <K> type of key
 * @param <HK> type of hash key
 * @param <HV> type of hash value
 */
public class DryRedisHash implements DryRedisCache {
	
	private final Map<String, Map<String, String>> store = new HashMap<String, Map<String, String>>();
	
	// commands from redis
	
	public int hdel(String key, String field) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return 0;
		}
		
		String removed = map.remove(field);
		if(removed == null) {
			return 0;
		}
		
		return 1;
	}
	
	public int hdel(String key, List<String> fields) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return 0;
		}
		
		int count = 0;
		for(String field : fields) {
			String removed = map.remove(field);
			if(removed != null) {
				count++;
			}
		}
		
		return count;
	}
	
	public int hexists(String key, String field) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return 0;
		}
		
		boolean has = map.containsKey(field);
		if(has) {
			return 1;
		}

		return 0;
	}
	
	public Object hget(String key, String field) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return null;
		}
		
		return map.get(field);
	}
	
	public List<String> hgetall(String key) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return null;
		}
		
		List<String> list = new ArrayList<String>();
		for(Entry<String, String> entry : map.entrySet()) {
			list.add(entry.getKey());
			list.add(entry.getValue());
		}
		
		return list;
	}
	
	public List<String> hkeys(String key) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return null;
		}

		return new ArrayList<String>(map.keySet());
	}
	
	public int hlen(String key) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return 0;
		}
		
		return map.size();
	}
	
	public int hset(String key, String field, String value) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			map = new HashMap<String, String>();
			this.store.put(key, map);
		}
		
		int returnValue = 1;
		if(map.containsKey(field)) {
			returnValue = 0;
		}
		
		map.put(field, value);
		return returnValue;
	}
	
	public int hsetnx(String key, String field, String value) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			map = new HashMap<String, String>();
			this.store.put(key, map);
		}
		
		if(map.containsKey(field)) {
			return 0;
		}
		
		map.put(field, value);
		return 1;
	}
	
	public int hstrlen(String key, String field) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return 0;
		}
		
		String value = map.get(field);
		if(value == null) {
			return 0;
		}
		
		return value.length();
	}
	
	public List<String> hvals(String key) {
		List<String> list = new ArrayList<String>();
		
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return list;
		}
		
		if(map.isEmpty()) {
			return list;
		}
		
		list.addAll(map.values());
		return list;
	}
	
	public long hincrby(String key, String field, long increment) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			map = new HashMap<String, String>();
			this.store.put(key, map);
		}
		
		String value = map.get(field);
		if(value == null) {
			value = "0";
		}
		
		long newValue = Long.parseLong(value) + increment;
		map.put(field, String.valueOf(newValue));
		return newValue;
	}
	
	public double hincrbyfloat(String key, String field, double increment) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			map = new HashMap<String, String>();
			this.store.put(key, map);
		}
		
		String value = map.get(field);
		if(value == null) {
			value = "0";
		}
		
		double newValue = Double.parseDouble(value) + increment;
		map.put(field, String.valueOf(newValue));
		return newValue;
	}
	
	public List<String> hmget(String key, List<String> fields) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return null;
		}
		
		List<String> list = new ArrayList<String>();
		for(String field : fields) {
			list.add(map.get(field));
		}
		
		return list;
	}
	
	public String hmset(String key, Map<String, String> fieldValues) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			map = new HashMap<String, String>();
			this.store.put(key, map);
		}
		
		map.putAll(fieldValues);
		return "OK";
	}
	
	// commands for DryRedisCache

	@Override
	public int del(String key) {
		Map<String, String> map = this.store.remove(key);
		if(map == null) {
			return 0;
		}
		
		return 1;
	}

	@Override
	public DryRedisCacheType getType() {
		return DryRedisCacheType.Hash;
	}

}
