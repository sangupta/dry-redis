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
import java.util.Set;

public interface DryRedisSetOperations {

    int sadd(String key, String value);

    int sadd(String key, List<String> values);

    int scard(String key);

    Set<String> sdiff(String key, String... otherKeys);

    int sdiffstore(String destination, String key, String... otherKeys);

    Set<String> sinter(String key, String... otherKeys);

    int sinterstore(String destination, String key, String... otherKeys);

    int sismember(String key, String value);

    Set<String> smembers(String key);

    int smove(String source, String destination, String value);

    String spop(String key);

    List<String> spop(String key, int count);

    String srandmember(String key);

    List<String> srandmember(String key, int count);

    int srem(String key, String value);

    int srem(String key, List<String> values);

    Set<String> sunion(String key, String... otherKeys);

    int sunionstore(String destination, String key, String... otherKeys);

    List<String> sscan(String key, int cursor);

}
