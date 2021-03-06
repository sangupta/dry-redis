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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sangupta.dryredis.support.DryRedisBitOperation;
import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;
import com.sangupta.dryredis.support.DryRedisUtils;

class DryRedisString extends DryRedisAbstractCache<String> implements DryRedisCache, DryRedisStringOperations {
	
	@Override
    public int append(String key, String value) {
		String oldValue = this.store.get(key);
		if(oldValue == null) {
	        this.store.put(key, value);
			return value.length();
		}
		
		String newValue = oldValue +  value;
		this.store.put(key, newValue);
		return newValue.length();
	}
	
	@Override
    public long incr(String key) {
		return this.incrby(key, 1);
	}
	
	@Override
    public long incrby(String key, long delta) {
		String value = this.store.get(key);
		long longValue = 0;
		if(value == null) {
			longValue = 0 + delta;
		} else {
			longValue = Long.parseLong(value) + delta;
		}
		
		this.store.put(key, String.valueOf(longValue));
		return longValue;
	}
	
	@Override
    public double incrbyfloat(String key, double delta) {
		String value = this.store.get(key);
		double doubleValue = 0d;
		if(value == null) {
			doubleValue = 0 + delta;
		} else {
			doubleValue = Double.parseDouble(value) + delta;
		}
		
		this.store.put(key, String.valueOf(doubleValue));
		return doubleValue;
	}
	
	@Override
    public List<String> mget(String[] keys) {
	    if(keys == null || keys.length == 0) {
            return null;
        }
	    
		List<String> list = new ArrayList<String>();
		for(String key : keys) {
			String value = this.store.get(key);
			list.add(value);
		}
		
		return list;
	}
	
	@Override
    public List<String> mget(Collection<String> keys) {
	    if(keys == null || keys.isEmpty()) {
	        return null;
	    }
	    
		List<String> list = new ArrayList<String>();
		for(String key : keys) {
			String value = this.store.get(key);
			list.add(value);
		}
		
		return list;
	}
	
	@Override
    public String set(String key, String value) {
		this.store.put(key, value);
		return "OK";
	}
	
	@Override
    public String setnx(String key, String value) {
		String oldValue = this.store.get(key);
		if(oldValue == null) {
	        this.store.put(key, value);
			return "OK";
		}
		
		return null;
	}
	
	@Override
    public String setxx(String key, String value) {
		if(!this.store.containsKey(key)) {
			return null;
		}
		
		this.store.put(key, value);
		return "OK";
	}
	
	@Override
    public String getrange(String key, int start, int end) {
		String value = this.store.get(key);
		if(value == null) {
			return "";
		}
		
		if(value.isEmpty()) {
			return "";
		}
		
		// negative values
		int size = value.length();
		if(start < 0) {
			start = size + start;
		}
		if(end < 0) {
			end = size + end;
		}
		
		// end is inclusive in redis
		end++;
		
		// bounds check
		if(start > size) {
			return "";
		}
		if(end > size) {
			end = size;
		}
		
		if(start > end) {
		    return "";
		}
		
		// return substring
		return value.substring(start, end);
	}
	
	@Override
    public int setrange(String key, int offset, String value) {
        if(offset < 0) {
            throw new IllegalArgumentException("Offset cannot be less than zero. Refer REDIS documentation.");
        }
        
		String existing = this.store.get(key);
		if(existing == null) {
			existing = "";
		}
		
		char[] source = existing.toCharArray();
		int size = offset + value.length();
		if(source.length < size) {
			source = Arrays.copyOf(source, size);
		}
		
		char[] chars = value.toCharArray();
		for(int index = offset; index < size; index++) {
			source[index] = chars[index - offset];
		}
		
		this.store.put(key, new String(source));
		return source.length;
	}
	
	@Override
    public long bitcount(String key) {
	    return this.bitcount(key, 0, key.length());
	}
	
	@Override
    public long bitcount(String key, int start, int end) {
		String value = this.getrange(key, start, end);
		if(value == null) {
			return 0;
		}
		
		if(value.isEmpty()) {
			return 0;
		}
		
		long bits = 0;
		for(int index = 0; index < value.length(); index++) {
			char c = value.charAt(index);
			bits += Integer.bitCount(c);
		}
		
		return bits;
	}
	
	@Override
    public long decr(String key) {
		return this.incrby(key, -1);
	}
	
	@Override
    public long decrby(String key, long delta) {
		return this.incrby(key, -delta);
	}
	
	@Override
    public String get(String key) {
		return this.store.get(key);
	}
	
	@Override
    public int strlen(String key) {
		String value = this.store.get(key);
		if(value == null) {
			return 0;
		}
		
		return value.length();
	}
	
	@Override
    public String getset(String key, String value) {
		String oldValue = this.store.get(key);
		this.store.put(key, value);
		return oldValue;
	}
	
    @Override
    public int bitop(DryRedisBitOperation operation, String destinationKey, String sourceKey, String... otherKeys) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public int bitpos(String key, boolean onOrOff) {
        String value = this.store.get(key);
        if(key == null || key.isEmpty()) {
            if(onOrOff) {
                return -1;
            }
            
            return 0;
        }
        
        byte[] bytes = value.getBytes();
        return DryRedisUtils.getNextBit(bytes, onOrOff, 0, bytes.length);
    }

    @Override
    public int bitpos(String key, boolean onOrOff, int startByte, int endByte) {
        String value = this.store.get(key);
        if(key == null || key.isEmpty()) {
            if(onOrOff) {
                return -1;
            }
            
            return 0;
        }
        
        byte[] bytes = value.getBytes();
        return DryRedisUtils.getNextBit(bytes, onOrOff, startByte, endByte);
    }

    @Override
    public int getbit(String key, long offset) {
        String value = this.store.get(key);
        if(key == null || key.isEmpty()) {
            return 0;
        }
        
        byte[] bytes = value.getBytes();
        int byteNum = (int) (offset / 8);
        if(byteNum >= bytes.length) {
            return 0;
        }
        
        byte bite = bytes[byteNum];
        int bit = (int) (offset % 8);
        return (bite >> bit) & 1;
    }

    @Override
    public int setbit(String key, long offset, boolean onOrOff) {
        throw new RuntimeException("not yet implemented");
    }

	// interface commands

	@Override
	public DryRedisCacheType getType() {
		return DryRedisCacheType.STRING;
	}

    @Override
    public String mset(Map<String, String> values) {
        if(values == null || values.isEmpty()) {
            return "OK";
        }
        
        for(Entry<String, String> entry : values.entrySet()) {
            this.store.put(entry.getKey(), entry.getValue());
        }
        
        return "OK";
    }

    @Override
    public int msetnx(Map<String, String> values) {
        if(values == null || values.isEmpty()) {
            return 0;
        }
        
        Set<String> keys = values.keySet();
        for(String key : keys) {
            if(this.hasKey(key)) {
                return 0;
            }
        }
        
        for(String key : keys) {
            this.store.put(key, values.get(key));
        }
        
        return 1;
    }

    @Override
    public String setex(String key, long secondsToExpire, String value) {
        this.set(key, value);
        this.pexpireat(key, System.currentTimeMillis() + secondsToExpire * 1000l);
        return "OK";
    }

    @Override
    public String psetex(String key, long milliSecondsToExpire, String value) {
        this.set(key, value);
        this.pexpireat(key, System.currentTimeMillis() + milliSecondsToExpire);
        return "OK";
    }

}
