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

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.dryredis.cache.DryRedisGeoOperations;
import com.sangupta.dryredis.cache.impl.TestDryRedisGeo;
import com.sangupta.dryredis.support.DryRedisGeoUnit;

/**
 * Test {@link DryRedisGeoOperations} using {@link DryRedis} instance.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisGeoOperations extends TestDryRedisGeo {
    
//    @Test
//    public void testGEOADD() {
//        DryRedisGeoOperations redis = getRedis();
//        
//        Assert.assertEquals(1, redis.geoadd("Sicily", 13.361389d, 38.115556d, "Palermo"));
//        Assert.assertEquals(1, redis.geoadd("Sicily", 15.087269d, 37.502669d, "Catania"));
//    }
//    
//    @Test
//    public void testGEODIST() {
//        DryRedisGeoOperations redis = getRedis();
//        
//        redis.geoadd("Sicily", 13.361389d, 38.115556d, "Palermo");
//        redis.geoadd("Sicily", 15.087269d, 37.502669d, "Catania");
//        
//        Assert.assertEquals(166274.15156960033d, redis.geodist("Sicily", "Palermo", "Catania", DryRedisGeoUnit.Meters), 0.1d);
//        
//    }
//    
//    @Test
//    public void testGEORADIUS() {
//        DryRedisGeoOperations redis = getRedis();
//        
//        redis.geoadd("Sicily", 13.361389d, 38.115556d, "Palermo");
//        redis.geoadd("Sicily", 15.087269d, 37.502669d, "Catania");
//        
//        Assert.assertTrue(TestUtils.equalUnsorted(TestUtils.asList("Catania"), redis.georadius("Sicily", 15d, 37d, 100, DryRedisGeoUnit.KiloMeters)));
//        Assert.assertTrue(TestUtils.equalUnsorted(TestUtils.asList("Catania", "Palermo"), redis.georadius("Sicily", 15d, 37d, 200, DryRedisGeoUnit.KiloMeters)));
//    }
    
    @Override
    protected DryRedisGeoOperations getRedis() {
        DryRedis redis = DryRedis.getDatabase();
        redis.flushdb();
        return redis;
    }

}