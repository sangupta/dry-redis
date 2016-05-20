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

import com.sangupta.dryredis.support.RedisGeoHash;

/**
 * Defines a Geographical point that has a latitude and a longitude.
 * 
 * @author sangupta
 *
 */
public class DryRedisGeoPoint {
    
    public static final int THOUSAND = 1000;

    public static final int MILLION = THOUSAND * THOUSAND;
    
    public String name;
    
    public double latitude;
    
    public double longitude;

    public DryRedisGeoPoint(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public String getGeoHash() {
        return RedisGeoHash.hash(this.latitude, this.longitude);
    }

    public double[] getPoint() {
        double[] array = new double[2];
        array[0] = this.latitude;
        array[1] = this.longitude;
        
        return array;
    }
    
    @Override
    public String toString() {
        return "[GeoPoint: " + this.name + ", lat: " + this.latitude + ", lon: " + this.longitude + "]";
    }
    
    @Override
    public int hashCode() {
        return Double.valueOf(this.latitude).intValue() * MILLION + Double.valueOf(this.longitude).intValue() * THOUSAND;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        
        if(this == obj) {
            return true;
        }
        
        if(!(obj instanceof DryRedisGeoPoint)) {
            return false;
        }
        
        DryRedisGeoPoint gp = (DryRedisGeoPoint) obj;
        return this.latitude == gp.latitude && this.longitude == gp.longitude;
    }
}
