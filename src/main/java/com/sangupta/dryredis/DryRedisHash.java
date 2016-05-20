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
class DryRedisHash extends DryRedisAbstractCache<Map<String, String>> implements DryRedisCache, DryRedisHashOperations {
	
	// commands from redis
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hdel(java.lang.String, java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hdel(java.lang.String, java.util.List)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hexists(java.lang.String, java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hget(java.lang.String, java.lang.String)
     */
	@Override
    public Object hget(String key, String field) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return null;
		}
		
		return map.get(field);
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hgetall(java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hkeys(java.lang.String)
     */
	@Override
    public List<String> hkeys(String key) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return null;
		}

		return new ArrayList<String>(map.keySet());
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hlen(java.lang.String)
     */
	@Override
    public int hlen(String key) {
		Map<String, String> map = this.store.get(key);
		if(map == null) {
			return 0;
		}
		
		return map.size();
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hset(java.lang.String, java.lang.String, java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hsetnx(java.lang.String, java.lang.String, java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hstrlen(java.lang.String, java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hvals(java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hincrby(java.lang.String, java.lang.String, long)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hincrbyfloat(java.lang.String, java.lang.String, double)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hmget(java.lang.String, java.util.List)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisHashOperations#hmset(java.lang.String, java.util.Map)
     */
	@Override
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
	public DryRedisCacheType getType() {
		return DryRedisCacheType.HASH;
	}

}
