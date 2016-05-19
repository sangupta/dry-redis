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

package com.sangupta.dryredis.cache;

import java.util.Collection;
import java.util.List;

public interface DryRedisStringOperations {

    int append(String key, String value);

    long incr(String key);

    long incrby(String key, long delta);

    double incrbyfloat(String key, double delta);

    List<String> mget(String[] keys);

    List<String> mget(Collection<String> keys);

    String set(String key, String value);

    String setnx(String key, String value);

    String setxx(String key, String value);

    String getrange(String key, int start, int end);

    int setrange(String key, int offset, String value);

    long bitcount(String key);

    long bitcount(String key, int start, int end);

    long decr(String key);

    long decrby(String key, long delta);

    String get(String key);

    int strlen(String key);

    String getset(String key, String value);

}