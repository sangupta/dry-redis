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

import java.util.List;
import java.util.Map;

public interface DryRedisHashOperations {

    public int hdel(String key, String field);

    public int hdel(String key, List<String> fields);

    public int hexists(String key, String field);

    public Object hget(String key, String field);

    public List<String> hgetall(String key);

    public List<String> hkeys(String key);

    public int hlen(String key);

    public int hset(String key, String field, String value);

    public int hsetnx(String key, String field, String value);

    public int hstrlen(String key, String field);

    public List<String> hvals(String key);

    public long hincrby(String key, String field, long increment);

    public double hincrbyfloat(String key, String field, double increment);

    public List<String> hmget(String key, List<String> fields);

    public String hmset(String key, Map<String, String> fieldValues);

}