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

package com.sangupta.dryredis.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.List;

import com.sangupta.dryredis.DryRedis;

public class DryRedisUtils {
    
    private static final Charset UTF_8 = Charset.forName("UTF-8"); 

	public static <V> List<V> subList(List<V> list, int start, int stop) {
		return list.subList(start, stop);
	}

	public static byte[] createDump(DryRedisCacheType cacheType, String key, Object value) {
	    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	    
	    outStream.write(DryRedis.DRY_REDIS_DUMP_VERSION);
	    outStream.write(cacheType.getCode());
	    
	    // write the key
	    outStream.write(key.length());
	    try {
            outStream.write(key.getBytes(UTF_8));
        } catch (IOException e) {
            // eat up
        }
	    
	    // write a dummy length for now
	    final int pointer = outStream.size();
	    outStream.write(0);
	    
	    // write the object itself
	    try {
            ObjectOutputStream objectStream = new ObjectOutputStream(outStream);
            objectStream.writeObject(value);
            objectStream.close();
        } catch (IOException e) {
            // eat up
        }
	    
	    final int bytesWrittenForObject = outStream.size() - pointer;
	    
	    byte[] bytes = outStream.toByteArray();
	    
	    // TODO: write the bytes written for object to the
	    
	    // TODO: write and compute checksum
	    
	    return bytes;
	}

	public static boolean wildcardMatch(String string, String pattern) {
        int i = 0;
        int j = 0;
        int starIndex = -1;
        int iIndex = -1;

        while (i < string.length()) {
            if (j < pattern.length() && (pattern.charAt(j) == '?' || pattern.charAt(j) == string.charAt(i))) {
                ++i;
                ++j;
            } else if (j < pattern.length() && pattern.charAt(j) == '*') {
                starIndex = j;
                iIndex = i;
                j++;
            } else if (starIndex != -1) {
                j = starIndex + 1;
                i = iIndex+1;
                iIndex++;
            } else {
                return false;
            }
        }

        while (j < pattern.length() && pattern.charAt(j) == '*') {
            ++j;
        }

        return j == pattern.length();
    }

    public static int getNextBit(byte[] bytes, boolean onOrOff, int start, int end) {
        throw new RuntimeException("not yet implemented");
    }
	
}
