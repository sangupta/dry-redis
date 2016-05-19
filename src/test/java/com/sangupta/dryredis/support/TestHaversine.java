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

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link Haversine} class.
 * 
 * @author sangupta
 *
 */
public class TestHaversine {

    @Test
    public void testGetGeoDistance() {
        // only lat-change
        double distance = Haversine.distance(28.545143900000003d, 77.3298117d, 48.545143900000003d, 77.3298117d);
        Assert.assertEquals(2223.898d, distance, 0.1d);
        
        // only long-change
        distance = Haversine.distance(28.545143900000003d, 77.3298117d, 28.545143900000003d, 57.3298117d);
        Assert.assertEquals(1951.278d, distance, 0.1d);
        
        // both lat-long
        distance = Haversine.distance(28.545143900000003d, 77.3298117d, 48.545143900000003d, 57.3298117d);
        Assert.assertEquals(2805.199d, distance, 0.1d);
    }
    
}