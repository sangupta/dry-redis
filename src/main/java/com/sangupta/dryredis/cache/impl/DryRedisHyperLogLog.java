package com.sangupta.dryredis.cache.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sangupta.dryredis.ds.HyperLogLog;
import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;

public class DryRedisHyperLogLog implements DryRedisCache {
	
	private final Map<String, HyperLogLog> store = new HashMap<String, HyperLogLog>();
	
	// redis commands
	
	public int pfadd(String key) {
		if(this.store.containsKey(key)) {
			return 0;
		}
		
		HyperLogLog hll = getNewHLL();
		this.store.put(key, hll);
		return 1;
	}
	
	public int pfadd(String key, String element) {
		HyperLogLog hll = this.store.get(key);
		if(hll == null) {
			hll = getNewHLL();
			this.store.put(key, hll);
		}
		
		hll.offer(element);
		return 1;
	}
	
	public long pfcount(String key) {
		HyperLogLog hll = this.store.get(key);
		if(hll == null) {
			return 0;
		}
		
		return hll.cardinality();
	}
	
	public long pfcount(List<String> keys) {
		if(keys == null || keys.isEmpty()) {
			throw new IllegalArgumentException("Keys cannot be empty");
		}
		
		if(keys.size() == 1) {
			return this.pfcount(keys.get(0));
		}
		
		List<HyperLogLog> estimators = new ArrayList<HyperLogLog>();
		for(String key : keys) {
			HyperLogLog hll = this.store.get(key);
			if(hll != null) {
				estimators.add(hll);
			}
		}
		
		int size = estimators.size();
		
		if(size == 0) {
			return 0;
		}
		
		if(size == 1) {
			return estimators.get(0).cardinality();
		}
		
		HyperLogLog cloned = estimators.get(0).clone();
		for(int index = 1; index < estimators.size(); index++) {
			cloned.merge(estimators.get(index));
		}
		
		return cloned.cardinality();
	}
	
	public String pfmerge(String destination, List<String> keys) {
		if(keys == null || keys.isEmpty()) {
			throw new IllegalArgumentException("Keys cannot be empty");
		}
		
		if(keys.size() == 1) {
			HyperLogLog hll = this.store.get(keys.get(0));
			if(hll == null) {
				this.store.put(destination, getNewHLL());
				return "OK";
			}
			
			this.store.put(destination, hll.clone());
			return "OK";
		}
		
		List<HyperLogLog> estimators = new ArrayList<HyperLogLog>();
		for(String key : keys) {
			HyperLogLog hll = this.store.get(key);
			if(hll != null) {
				estimators.add(hll);
			}
		}
		
		int size = estimators.size();
		
		if(size == 0) {
			this.store.put(destination, getNewHLL());
			return "OK";
		}
		
		if(size == 1) {
			this.store.put(destination, estimators.get(0).clone());
			return "OK";
		}
		
		HyperLogLog cloned = estimators.get(0).clone();
		for(int index = 1; index < estimators.size(); index++) {
			cloned.merge(estimators.get(index));
		}
		
		this.store.put(destination, cloned);
		return "OK";
	}

	/**
	 * Method to return a new instance of {@link HyperLogLog} instance. We use this
	 * method rather than creating a new instance every place. This allows us to keep
	 * the standard deviation factor under control.
	 * 
	 * @return
	 */
	private HyperLogLog getNewHLL() {
		return new HyperLogLog(0.81d);
	}
	
	// for interface

	@Override
	public int del(String key) {
		HyperLogLog hll = this.store.remove(key);
		if(hll == null) {
			return 0;
		}
		
		return 1;
	}

	@Override
	public DryRedisCacheType getType() {
		return DryRedisCacheType.HYPER_LOG_LOG;
	}

    @Override
    public boolean hasKey(String key) {
        return this.store.containsKey(key);
    }
    
    @Override
    public void keys(String pattern, List<String> keys) {
        
    }
}
