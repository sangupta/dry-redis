package com.sangupta.dryredis.cache.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sangupta.dryredis.cache.DryRedisGeoOperations;
import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;
import com.sangupta.dryredis.support.DryRedisGeoUnit;
import com.sangupta.dryredis.support.DryRedisUtils;
import com.sangupta.dryredis.support.RedisGeoHash;
import com.sangupta.dryredis.support.Haversine;

public class DryRedisGeo implements DryRedisCache, DryRedisGeoOperations {
	
	private final Map<String, Map<String, GeoPoint>> store = new HashMap<String, Map<String, GeoPoint>>();
	
	// redis commands
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisGeoOperations#geoadd(java.lang.String, double, double, java.lang.String)
     */
	@Override
    public int geoadd(String key, double longitude, double latitude, String member) {
		Map<String, GeoPoint> points = this.store.get(key);
		int result = 0;
		
		if(points == null) {
			points = new HashMap<String, GeoPoint>();
			this.store.put(key, points);
		}
		
        if(!points.containsKey(member)) {
            result = 1;
        }
		points.put(member, new GeoPoint(member, latitude, longitude));
		return result;
	}
	
	/* (non-Javadoc)
     * @see com.sangupta.dryredis.DryRedisGeoOperations#geohash(java.lang.String, java.lang.String)
     */
	@Override
    public String geohash(String key, String member) {
		Map<String, GeoPoint> points = this.store.get(key);
		if(points == null) {
			return null;
		}
		
		GeoPoint point = points.get(member);
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
		Map<String, GeoPoint> points = this.store.get(key);
		if(points == null) {
			return null;
		}
		
		GeoPoint geoPoint = points.get(member);
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
		Map<String, GeoPoint> points = this.store.get(key);
		if(points == null) {
			return null;
		}
		
		GeoPoint point1 = points.get(member1);
		GeoPoint point2 = points.get(member2);
		
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
		GeoPoint origin = new GeoPoint("origin", latitude, longitude);
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
		Map<String, GeoPoint> points = this.store.get(key);
		if(points == null) {
			return null;
		}
		
		GeoPoint origin = points.get(member);
		if(origin == null) {
			return null;
		}
		
		return this.getUsingRadius(key, origin, radius, unit, withCoordinates, withDistance, withHash, count);
	}
	
	private List<String> getUsingRadius(String key, GeoPoint origin, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count) {
		Map<String, GeoPoint> points = this.store.get(key);
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
		for(GeoPoint point : points.values()) {
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
	public int del(String key) {
		Map<String, GeoPoint> removed = this.store.remove(key);
		if(removed == null) {
			return 0;
		}
		
		return 1;
	}

	@Override
	public DryRedisCacheType getType() {
		return DryRedisCacheType.GEO;
	}

    @Override
    public boolean hasKey(String key) {
        return this.store.containsKey(key);
    }
    
    @Override
    public void keys(String pattern, List<String> keys) {
        
    }
    
    @Override
    public byte[] dump(String key) {
        return DryRedisUtils.createDump(this.getType(), key, this.store.get(key));
    }

    /**
     * Class that represents a geo-point.
     * 
     * @author sangupta
     *
     */
	public static class GeoPoint {

		public String name;
		
		public double latitude;
		
		public double longitude;

		public GeoPoint(String name, double latitude, double longitude) {
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
	}
	
}
