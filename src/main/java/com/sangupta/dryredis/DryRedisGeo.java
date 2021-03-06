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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;
import com.sangupta.dryredis.support.DryRedisGeoPoint;
import com.sangupta.dryredis.support.DryRedisGeoUnit;
import com.sangupta.dryredis.support.Haversine;

class DryRedisGeo extends DryRedisAbstractCache<Map<String, DryRedisGeoPoint>> implements DryRedisCache, DryRedisGeoOperations {
	
	// redis commands
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisGeoOperations#geoadd(java.lang.String, double, double, java.lang.String)
     */
	@Override
    public int geoadd(String key, double longitude, double latitude, String member) {
		Map<String, DryRedisGeoPoint> points = this.store.get(key);
		int result = 0;
		
		if(points == null) {
			points = new HashMap<String, DryRedisGeoPoint>();
			this.store.put(key, points);
		}
		
        if(!points.containsKey(member)) {
            result = 1;
        }
		points.put(member, new DryRedisGeoPoint(member, latitude, longitude));
		return result;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisGeoOperations#geohash(java.lang.String, java.lang.String)
     */
	@Override
    public String geohash(String key, String member) {
		Map<String, DryRedisGeoPoint> points = this.store.get(key);
		if(points == null) {
			return null;
		}
		
		DryRedisGeoPoint point = points.get(member);
		if(point == null) {
			return null;
		}
		
		return point.getGeoHash();
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisGeoOperations#geopos(java.lang.String, java.lang.String)
     */
	@Override
    public double[] geopos(String key, String member) {
		Map<String, DryRedisGeoPoint> points = this.store.get(key);
		if(points == null) {
			return null;
		}
		
		DryRedisGeoPoint geoPoint = points.get(member);
		if(geoPoint == null) {
			return null;
		}
		
		return geoPoint.getPoint();
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisGeoOperations#geodist(java.lang.String, java.lang.String, java.lang.String, com.sangupta.dryredis.support.DryRedisGeoUnit)
     */
	@Override
    public Double geodist(String key, String member1, String member2, DryRedisGeoUnit unit) {
		Map<String, DryRedisGeoPoint> points = this.store.get(key);
		if(points == null) {
			return null;
		}
		
		DryRedisGeoPoint point1 = points.get(member1);
		DryRedisGeoPoint point2 = points.get(member2);
		
		if(point1 == null || point2 == null) {
			return null;
		}
		
		double distance = Haversine.distance(point1.latitude, point1.longitude, point2.latitude, point2.longitude);
		
		switch(unit) {
			case Feet:
				return asFeet(distance);
			
			case KiloMeters:
				return distance;
			
			case Meters:
				return asMeters(distance);
			
			case Miles:
				return asMiles(distance);

			default:
				break;
		}
		
		return null;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisGeoOperations#georadius(java.lang.String, double, double, double, com.sangupta.dryredis.support.DryRedisGeoUnit)
     */
	@Override
    public List<String> georadius(String key, double longitude, double latitude, double radius, DryRedisGeoUnit unit) {
		return this.georadius(key, latitude, longitude, radius, unit, false, false, false, 0);
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisGeoOperations#georadius(java.lang.String, double, double, double, com.sangupta.dryredis.support.DryRedisGeoUnit, boolean, boolean, boolean, int)
     */
	@Override
    public List<String> georadius(String key, double longitude, double latitude, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count) {
		DryRedisGeoPoint origin = new DryRedisGeoPoint("origin", latitude, longitude);
		return this.getUsingRadius(key, origin, radius, unit, withCoordinates, withDistance, withHash, count);
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisGeoOperations#georadiusbymember(java.lang.String, java.lang.String, double, com.sangupta.dryredis.support.DryRedisGeoUnit)
     */
	@Override
    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit) {
		return this.georadiusbymember(key, member, radius, unit, false, false, false, 0);
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisGeoOperations#georadiusbymember(java.lang.String, java.lang.String, double, com.sangupta.dryredis.support.DryRedisGeoUnit, boolean, boolean, boolean, int)
     */
	@Override
    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count) {
		Map<String, DryRedisGeoPoint> points = this.store.get(key);
		if(points == null) {
			return null;
		}
		
		DryRedisGeoPoint origin = points.get(member);
		if(origin == null) {
			return null;
		}
		
		return this.getUsingRadius(key, origin, radius, unit, withCoordinates, withDistance, withHash, count);
	}
	
	private List<String> getUsingRadius(String key, DryRedisGeoPoint origin, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count) {
		Map<String, DryRedisGeoPoint> points = this.store.get(key);
		if(points == null) {
			return null;
		}
		
		List<String> result = new ArrayList<String>();
		if(points.isEmpty()) {
			return result;
		}
		
		// convert radius to kilometers to work with
		if(unit != DryRedisGeoUnit.KiloMeters) {
			radius = toKilometers(radius, unit);
		}
		
		int pointsFound = 0;
		for(DryRedisGeoPoint point : points.values()) {
			double distance = Haversine.distance(origin.latitude, origin.longitude, point.latitude, point.longitude);
			if(distance < radius) {
				result.add(point.name);
				pointsFound++;
				
				if(withCoordinates) {
					result.add(String.valueOf(point.latitude));
					result.add(String.valueOf(point.longitude));
				}
				
				if(withDistance) {
					result.add(String.valueOf(distance));
				}
				
				if(withHash) {
					result.add(point.getGeoHash());
				}
				
				if(count > 0) {
					if(pointsFound == count) {
						// we have enough matches
						break;
					}
				}
			}
		}
		
		return result;
	}
	
	private double toKilometers(double radius, DryRedisGeoUnit unit) {
		switch(unit) {
			case Feet:
				return radius * 0.0003048d;
			
			case KiloMeters:
				return radius;
				
			case Meters:
				return radius / 1000d;
				
			case Miles:
				return radius * 1.60934d;
				
			default:
		
		}
		
		throw new IllegalArgumentException("Radius is not in a unit that is understood yet.");
	}

	private double asMiles(double distanceInKilometers) {
		return distanceInKilometers / 1.60934d;
	}

	private double asMeters(double distanceInKilometers) {
		return distanceInKilometers * 1000d;
	}

	private double asFeet(double distanceInKilometers) {
		return distanceInKilometers * 3280.83174048556d;
	}

	// from interface

	@Override
	public DryRedisCacheType getType() {
		return DryRedisCacheType.GEO;
	}

}
