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

package com.sangupta.dryredis.cache.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sangupta.dryredis.cache.DryRedisListOperations;
import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;
import com.sangupta.dryredis.support.DryRedisInsertOrder;
import com.sangupta.dryredis.support.DryRedisUtils;

public class DryRedisList extends DryRedisAbstractCache<List<String>> implements DryRedisCache, DryRedisListOperations {
	
	private final Object blockingMonitor = new Object();
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#blpop(java.lang.String, int)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#brpop(java.lang.String, int)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#brpoplpush(java.lang.String, java.lang.String, int)
     */
	@Override
    public String brpoplpush(String source, String destination, int maxSecondsToBlock) {
		String value = this.brpop(source, maxSecondsToBlock);
		if(value == null) {
		    return null;
		}
		
		this.lpush(destination, value);
		return value;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#lindex(java.lang.String, int)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#linsert(java.lang.String, com.sangupta.dryredis.support.DryRedisInsertOrder, java.lang.String, java.lang.String)
     */
	@Override
    public int linsert(String key, DryRedisInsertOrder order, String pivot, String value) {
		List<String> list = this.store.get(key);
		if(list == null || list.isEmpty()) {
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#llen(java.lang.String)
     */
	@Override
    public int llen(String key) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return 0;
		}
		
		return list.size();
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#lpop(java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#lpush(java.lang.String, java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#lpush(java.lang.String, java.util.List)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#lpushx(java.lang.String, java.util.List)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#lrange(java.lang.String, int, int)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#lrem(java.lang.String, int, java.lang.String)
     */
	@Override
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
		count = 0 - count;
		int removed = 0;
		int size = list.size();
		for(int index = size - 1; index >= 0; index--) {
			String item = list.get(index);
			if(item.equals(value)) {
				list.remove(index);
				removed++;
				
				if(removed == count) {
				    return removed;
				}
			}
		}
		
		return removed;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#lset(java.lang.String, int, java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#rpop(java.lang.String)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#rpoplpush(java.lang.String, java.lang.String)
     */
	@Override
    public String rpoplpush(String source, String destination) {
		String value = this.rpop(source);
		this.lpush(destination, value);
		return value;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#rpush(java.lang.String, java.lang.String)
     */
	@Override
    public int rpush(String key, String value) {
		List<String> list = this.store.get(key);
		if(list == null) {
			list = new ArrayList<String>();
			this.store.put(key, list);
		}
		
		list.add(value);
		return list.size();
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#rpushx(java.lang.String, java.lang.String)
     */
	@Override
    public int rpushx(String key, String value) {
	    if(this.store.containsKey(key)) {
	        this.rpush(key, value);	        
	    }
	    
	    return this.llen(key);
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#rpush(java.lang.String, java.util.List)
     */
	@Override
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
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#lpushx(java.lang.String, java.lang.String)
     */
	@Override
    public int lpushx(String key, String value) {
		List<String> list = this.store.get(key);
		if(list == null) {
			return -1;
		}
		
		list.add(value);
//		this.blockingMonitor.notify();
		return list.size();
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisListOperations#ltrim(java.lang.String, int, int)
     */
	@Override
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
			stop = size + stop;
		}
		
		// check for bounds
		if(start > size) {
			this.store.remove(key);
			return "OK";
		}

		// for inclusive stop
        stop++;
        if(stop > size) {
            stop = size;
        }
		
		list = new ArrayList<String>(list.subList(start, stop));
		this.store.put(key, list);
		return "OK";
	}
	
	// commmands from DryRedisCache

	@Override
	public DryRedisCacheType getType() {
		return DryRedisCacheType.LIST;
	}

}
