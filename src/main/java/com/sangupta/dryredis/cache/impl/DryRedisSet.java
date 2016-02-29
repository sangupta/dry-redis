package com.sangupta.dryredis.cache.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;

public class DryRedisSet implements DryRedisCache {
	
	private final Map<String, Set<String>> store = new HashMap<String, Set<String>>();
	
	// commands from redis
	
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
	
	public int scard(String key) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			return 0;
		}
		
		return set.size();
	}
	
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
	
	public int sdiffstore(String destination, String key, String... otherKeys) {
		Set<String> setToStore = this.sdiff(key, otherKeys);
		this.store.put(destination, setToStore);
		return setToStore.size();
	}
	
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
	
	public int sinterstore(String destination, String key, String... otherKeys) {
		Set<String> setToStore = this.sinter(key, otherKeys);
		this.store.put(destination, setToStore);
		return setToStore.size();
	}
	
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
	
	public Set<String> smembers(String key) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			return null;
		}
		
		return new HashSet<String>(set);
	}
	
	public int smove(String source, String destination, String value) {
		int removed = this.srem(source, value);
		if(removed == 0) {
			return 0;
		}
		
		this.sadd(destination, value);
		return 1;
	}
	
	public String spop(String key) {
		List<String> values = this.spop(key, 1);
		if(values == null || values.isEmpty()) {
			return null;
		}
		
		return values.get(0);
	}
	
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
	
	public String srandmember(String key) {
	    List<String> list = this.srandmember(key, 1);
	    if(list == null || list.isEmpty()) {
	        return null;
	    }
	    
	    return list.get(0);
	}
	
	public List<String> srandmember(String key, int count) {
		Set<String> set = this.store.get(key);
		if(set == null) {
			return null;
		}
		
		if(count == 0) {
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
	
	public int sunionstore(String destination, String key, String... otherKeys) {
		Set<String> setToStore = this.sunion(key, otherKeys);
		this.store.put(destination, setToStore);
		return setToStore.size();
	}
	
	public List<String> sscan(String key, int cursor) {
		throw new RuntimeException("Not yet implemented");
	}
	
	// commands for DryRedisCache

	@Override
	public int del(String key) {
		Set<String> set = this.store.remove(key);
		if(set == null) {
			return 0;
		}
		
		return 1;
	}

	@Override
	public DryRedisCacheType getType() {
		return DryRedisCacheType.SET;
	}

    @Override
    public boolean hasKey(String key) {
        return this.store.containsKey(key);
    }
    
    @Override
    public void keys(String pattern, List<String> keys) {
        
    }
}
