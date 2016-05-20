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

import java.util.List;

import com.sangupta.dryredis.support.DryRedisInsertOrder;

interface DryRedisListOperations {

    String blpop(String key, int maxSecondsToBlock);

    String brpop(String key, int maxSecondsToBlock);

    String brpoplpush(String source, String destination, int maxSecondsToBlock);

    String lindex(String key, int index);

    int linsert(String key, DryRedisInsertOrder order, String pivot, String value);

    int llen(String key);

    String lpop(String key);

    int lpush(String key, String value);

    int lpush(String key, List<String> values);

    int lpushx(String key, List<String> values);

    /**
     * 
     * @param key
     * @param start inclusive
     * @param stop inclusive
     * @return
     */
    List<String> lrange(String key, int start, int stop);

    int lrem(String key, int count, String value);

    String lset(String key, int index, String value);

    String rpop(String key);

    String rpoplpush(String source, String destination);

    int rpush(String key, String value);

    int rpushx(String key, String value);

    int rpush(String key, List<String> value);

    int lpushx(String key, String value);

    String ltrim(String key, int start, int stop);

}
