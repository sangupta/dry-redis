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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;

class DryRedisSet extends DryRedisAbstractCache<Set<String>> implements DryRedisCache, DryRedisSetOperations {
	
	// commands from redis
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#sadd(java.lang.String, java.lang.String)
     */
	@Override
    public int sadd(String key, String value) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			set = new HashSet<String>();
			this.store.put(key, set);
		}
		
		boolean added = set.add(value);
		if(added) {
			return 1;
		}
		
		return 0;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#sadd(java.lang.String, java.util.List)
     */
	@Override
    public int sadd(String key, List<String> values) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			set = new HashSet<String>();
			this.store.put(key, set);
		}
		
		int added = 0;
		for(String value : values) {
			if(set.add(value)) {
				added++;
			}
		}
		
		return added;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#scard(java.lang.String)
     */
	@Override
    public int scard(String key) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			return 0;
		}
		
		return set.size();
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#sdiff(java.lang.String, java.lang.String)
     */
	@Override
    public Set<String> sdiff(String key, String... otherKeys) {
		Set<String> set = this.store.get(key);
		
		Set<String> clonedSet;
		if(set != null) {
			clonedSet = new HashSet<String>(set);
		} else {
			clonedSet = new HashSet<String>();
		}
		
		// find subtracting
		for(String otherKey : otherKeys) {
			Set<String> otherSet = this.store.get(otherKey);
			if(otherSet == null) {
				continue;
			}
			
			clonedSet.removeAll(otherSet);
		}
		
		return clonedSet;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#sdiffstore(java.lang.String, java.lang.String, java.lang.String)
     */
	@Override
    public int sdiffstore(String destination, String key, String... otherKeys) {
		Set<String> setToStore = this.sdiff(key, otherKeys);
		this.store.put(destination, setToStore);
		return setToStore.size();
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#sinter(java.lang.String, java.lang.String)
     */
	@Override
    public Set<String> sinter(String key, String... otherKeys) {
		Set<String> set = this.store.get(key);
		
		Set<String> clonedSet;
		if(set != null) {
			clonedSet = new HashSet<String>(set);
		} else {
			clonedSet = new HashSet<String>();
		}
		
		// find subtracting
		boolean foundOne = false;
		for(String otherKey : otherKeys) {
			Set<String> otherSet = this.store.get(otherKey);
			if(otherSet == null) {
				continue;
			}
			
			foundOne = true;
			clonedSet.retainAll(otherSet);
		}
		
		if(!foundOne) {
		    // clear existing set
		    clonedSet.clear();
		}
		
		return clonedSet;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#sinterstore(java.lang.String, java.lang.String, java.lang.String)
     */
	@Override
    public int sinterstore(String destination, String key, String... otherKeys) {
		Set<String> setToStore = this.sinter(key, otherKeys);
		this.store.put(destination, setToStore);
		return setToStore.size();
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#sismember(java.lang.String, java.lang.String)
     */
	@Override
    public int sismember(String key, String value) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			return 0;
		}
		
		if(set.contains(value)) {
			return 1;
		}
		
		return 0;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#smembers(java.lang.String)
     */
	@Override
    public Set<String> smembers(String key) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			return null;
		}
		
		return new HashSet<String>(set);
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#smove(java.lang.String, java.lang.String, java.lang.String)
     */
	@Override
    public int smove(String source, String destination, String value) {
		int removed = this.srem(source, value);
		if(removed == 0) {
			return 0;
		}
		
		this.sadd(destination, value);
		return 1;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#spop(java.lang.String)
     */
	@Override
    public String spop(String key) {
		List<String> values = this.spop(key, 1);
		if(values == null || values.isEmpty()) {
			return null;
		}
		
		return values.get(0);
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#spop(java.lang.String, int)
     */
	@Override
    public List<String> spop(String key, int count) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			return null;
		}
		
		Iterator<String> iterator = set.iterator();
		List<String> result = new ArrayList<String>();
		while(iterator.hasNext()) {
			String item = iterator.next();
			iterator.remove();
			result.add(item);
			
			if(result.size() == count) {
				return result;
			}
		}
		
		return result;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#srandmember(java.lang.String)
     */
	@Override
    public String srandmember(String key) {
	    List<String> list = this.srandmember(key, 1);
	    if(list == null || list.isEmpty()) {
	        return null;
	    }
	    
	    return list.get(0);
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#srandmember(java.lang.String, int)
     */
	@Override
    public List<String> srandmember(String key, int count) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			return null;
		}
		
		if(count <= 0) {
		    return null;
		}
		
		if(count > 0) {
		    Set<String> newSet = new HashSet<String>();
		    for(String s : set) {
		        newSet.add(s);
		        
		        if(newSet.size() == count) {
		            break;
		        }
		    }
		    
		    return new ArrayList<String>(newSet);
		}
		
		List<String> result = new ArrayList<String>();
		for(String item : set) {
			result.add(item);
			
			if(result.size() == count) {
				break;
			}
		}
		
		return result;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#srem(java.lang.String, java.lang.String)
     */
	@Override
    public int srem(String key, String value) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			return 0;
		}
		
		boolean removed = set.remove(value);
		if(removed) {
			return 1;
		}
		
		return 0;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#srem(java.lang.String, java.util.List)
     */
	@Override
    public int srem(String key, List<String> values) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			return 0;
		}
		
		int count = 0;
		for(String value : values) {
			if(set.remove(value)) {
				count++;
			}
		}
		
		return count;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#sunion(java.lang.String, java.lang.String)
     */
	@Override
    public Set<String> sunion(String key, String... otherKeys) {
		Set<String> set = this.store.get(key);
		
		Set<String> clonedSet;
		if(set != null) {
			clonedSet = new HashSet<String>(set);
		} else {
			clonedSet = new HashSet<String>();
		}
		
		// find subtracting
		for(String otherKey : otherKeys) {
			Set<String> otherSet = this.store.get(otherKey);
			if(otherSet == null) {
				continue;
			}
			
			clonedSet.addAll(otherSet);
		}
		
		return clonedSet;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#sunionstore(java.lang.String, java.lang.String, java.lang.String)
     */
	@Override
    public int sunionstore(String destination, String key, String... otherKeys) {
		Set<String> setToStore = this.sunion(key, otherKeys);
		this.store.put(destination, setToStore);
		return setToStore.size();
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSetOperations#sscan(java.lang.String, int)
     */
	@Override
    public List<String> sscan(String key, int cursor) {
		throw new RuntimeException("Not yet implemented");
	}
	
	// commands for DryRedisCache

	@Override
	public DryRedisCacheType getType() {
		return DryRedisCacheType.SET;
	}

}
