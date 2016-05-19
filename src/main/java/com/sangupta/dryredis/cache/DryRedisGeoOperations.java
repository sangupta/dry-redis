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

import com.sangupta.dryredis.support.DryRedisGeoUnit;

public interface DryRedisGeoOperations {

    public int geoadd(String key, double longitude, double latitude, String member);

    public String geohash(String key, String member);

    public double[] geopos(String key, String member);

    public Double geodist(String key, String member1, String member2, DryRedisGeoUnit unit);

    public List<String> georadius(String key, double longitude, double latitude, double radius, DryRedisGeoUnit unit);

    public List<String> georadius(String key, double longitude, double latitude, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count);

    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit);

    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count);

}