package com.sangupta.dryredis.cache.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;
import com.sangupta.dryredis.support.DryRedisInsertOrder;
import com.sangupta.dryredis.support.DryRedisUtils;

public class DryRedisList implements DryRedisCache {
	
	private final Map<String, List<String>> store = new HashMap<String, List<String>>();
	
	private final Object blockingMonitor = new Object();
	
	public String blpop(String key, int maxSecondsToBlock) {
	    final long millis = maxSecondsToBlock * 1000l;
	    final long end = System.currentTimeMillis() + millis;
		
	    do {
		    String result = this.lpop(key);
		    if(result != null) {
		        return result;
		    }
		    
            if(System.currentTimeMillis() > end) {
                return null;
            }

            try {
                this.blockingMonitor.wait(maxSecondsToBlock * 1000l);
            } catch (InterruptedException e) {
                return null;
            }
		} while(true);
	}
	
	public String brpop(String key, int maxSecondsToBlock) {
        final long millis = maxSecondsToBlock * 1000l;
        final long end = System.currentTimeMillis() + millis;
        
        do {
            String result = this.rpop(key);
            if(result != null) {
                return result;
            }
            
            if(System.currentTimeMillis() > end) {
                return null;
            }

            try {
                this.blockingMonitor.wait(maxSecondsToBlock * 1000l);
            } catch (InterruptedException e) {
                return null;
            }
        } while(true);
	}
	
	public String brpoplpush(String source, String destination, int maxSecondsToBlock) {
		String value = this.brpop(source, maxSecondsToBlock);
		if(value == null) {
		    return null;
		}
		
		this.lpush(destination, value);
		return value;
	}
	
	public String lindex(String key, int index) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return null;
		}
		
		if(index < 0) {
			index = list.size() + index;
		}
		
		if(index >= list.size()) {
		    return null;
		}
		
		return list.get(index);
	}
	
	public int linsert(String key, DryRedisInsertOrder order, String pivot, String value) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return 0;
		}
		
		int size = list.size();
		for(int index = 0; index < size; index++) {
			String item = list.get(index);
			
			if(item.equals(pivot)) {
				// insert depending on the order
				if(order == DryRedisInsertOrder.AFTER) {
					list.add(index + 1, value);
					return list.size();
				}
				
				if(order == DryRedisInsertOrder.BEFORE) {
					list.add(index, value);
					return list.size();
				}
			}
		}
		
		return -1;
	}
	
	public int llen(String key) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return 0;
		}
		
		return list.size();
	}
	
	public String lpop(String key) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return null;
		}
		
		if(list.isEmpty()) {
			return null;
		}
		
		return list.remove(0);
	}
	
	public int lpush(String key, String value) {
		List<String> list = this.store.get(key);
		if(list == null) {
			list = new ArrayList<String>();
			this.store.put(key, list);
		}
		
		list.add(0, value);
//		this.blockingMonitor.notify();
		return list.size();
	}
	
	public int lpush(String key, List<String> values) {
		List<String> list = this.store.get(key);
		if(list == null) {
			list = new ArrayList<String>();
			this.store.put(key, list);
		}
		
		for(String item : values) {
			list.add(0, item);
		}
		
//		this.blockingMonitor.notifyAll();
		return list.size();
	}
	
	public int lpushx(String key, List<String> values) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return -1;
		}
		
		for(String item : values) {
			list.add(0, item);
		}
		
//		this.blockingMonitor.notifyAll();
		return list.size();
	}
	
	/**
	 * 
	 * @param key
	 * @param start inclusive
	 * @param stop inclusive
	 * @return
	 */
	public List<String> lrange(String key, int start, int stop) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return null;
		}
		
		if(list.isEmpty()) {
			return new ArrayList<String>();
		}
		
		// convert start and end respectively
		int size = list.size();
		if(start < 0) {
			start = size + start;
		}
		if(stop < 0) {
			stop = size + stop;
		}
		
		// make stop inclusive as per redis documentation
		stop = stop + 1;
		
		// now check for bounds
		if(start < 0) {
			start = 0;
		}
		if(stop > size) {
			stop = size;
		}
		
		// return sub list
		return DryRedisUtils.subList(list, start, stop);
	}
	
	public int lrem(String key, int count, String value) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return 0;
		}
		
		if(list.isEmpty()) {
			return 0;
		}
		
		if(count >= 0) {
			// remove all or count from head to tail
			int removed = 0;
			
			Iterator<String> iterator = list.iterator();
			while(iterator.hasNext()) {
				String item = iterator.next();
				if(item.equals(value)) {
					iterator.remove();
					removed++;
					
					if(removed == count) {
						return removed;
					}
				}
			}
			
			return removed;
		}
		
		// this is the case of moving from tail to head
		int removed = 0;
		int size = list.size();
		for(int index = size - 1; index >= 0; index--) {
			String item = list.get(index);
			if(item.equals(value)) {
				list.remove(index);
				removed++;
			}
		}
		
		return removed;
	}
	
	public String lset(String key, int index, String value) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return "OK";
		}
		
		if(list.isEmpty()) {
			// refer redis docs for exception details
			throw new IndexOutOfBoundsException("List is empty");
		}
		
		// negative index
		int size = list.size();
		if(index < 0) {
			index = size + index;
		}
		
		list.set(index, value);
		return "OK";
	}
	
	public String rpop(String key) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return null;
		}
		
		if(list.isEmpty()) {
			return null;
		}
		
		return list.remove(list.size() - 1);
	}
	
	public String rpoplpush(String source, String destination) {
		String value = this.rpop(source);
		this.lpush(destination, value);
		return value;
	}
	
	public int rpush(String key, String value) {
		List<String> list = this.store.get(key);
		if(list == null) {
			list = new ArrayList<String>();
			this.store.put(key, list);
		}
		
		list.add(value);
		return list.size();
	}
	
	public int rpushx(String key, String value) {
	    if(this.store.containsKey(key)) {
	        this.rpush(key, value);	        
	    }
	    
	    return this.llen(key);
	}
	
	public int rpush(String key, List<String> value) {
		List<String> list = this.store.get(key);
		if(list == null) {
			list = new ArrayList<String>();
			this.store.put(key, list);
		}
		
		for(String item : value) {
			list.add(item);
		}
		
		return list.size();
	}
	
	public int lpushx(String key, String value) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return -1;
		}
		
		list.add(value);
//		this.blockingMonitor.notify();
		return list.size();
	}
	
	public String ltrim(String key, int start, int stop) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return "OK";
		}
		
		if(list.isEmpty()) {
			this.store.remove(key);
			return "OK";
		}
		
		int size = list.size();

		// negative bounds
		if(start < 0) {
			start = size + start;
		}
		if(stop < 0) {
			stop = size + start;
		}
		
		// check for bounds
		if(start > size) {
			this.store.remove(key);
			return "OK";
		}
		
		list = new ArrayList<String>(list.subList(start, stop));
		this.store.put(key, list);
		return "OK";
	}
	
	// commmands from DryRedisCache

	@Override
	public int del(String key) {
		List<String> list = this.store.remove(key);
		if(list == null) {
			return 0;
		}
		
		return 1;
	}

	@Override
	public DryRedisCacheType getType() {
		return DryRedisCacheType.LIST;
	}

    @Override
    public boolean hasKey(String key) {
        return this.store.containsKey(key);
    }
    
    @Override
    public void keys(String pattern, List<String> keys) {
        
    }
}
