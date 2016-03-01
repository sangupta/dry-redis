package com.sangupta.dryredis.support;

import java.util.List;

import com.sangupta.dryredis.cache.impl.DryRedisGeo.GeoPoint;

public class DryRedisUtils {
	
	private static final double RADIUS_OF_EARTH = 6371; // in kilometers

	public static <V> List<V> subList(List<V> list, int start, int stop) {
		// TODO: performance - fix this - remove problems with sublist
		return list.subList(start, stop);
	}
	
	public static double getGeoDistance(GeoPoint point1, GeoPoint point2) {
		if(point1.equals(point2)) {
			return 0d;
		}
		
		double latitudeDistance = degreeToRadians(point2.latitude - point1.latitude);
        double longitudeDistance = degreeToRadians(point2.longitude - point1.longitude);
        
        double a = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2) + 
                   Math.cos(degreeToRadians(point1.latitude)) * Math.cos(degreeToRadians(point2.latitude)) * 
                   Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = RADIUS_OF_EARTH * c;
        
        return distance;
	}

	public static double degreeToRadians(double angleInDegrees) {
        return angleInDegrees * Math.PI / 180.0d;
    }
	
	private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";
	
	public static String getGeoHash(double latitude, double longitude) {
		return encodeHash(latitude, longitude, 52);
	}
	
	private static String encodeHash(double latitude, double longitude, int length) {
		longitude = to180(longitude);
        return fromLongToString(encodeHashToLong(latitude, longitude, length));
	}
	
	private static double to180(double d) {
        if (d < 0)
            return -to180(Math.abs(d));
        else {
            if (d > 180) {
                long n = Math.round(Math.floor((d + 180) / 360.0));
                return d - n * 360;
            } else
                return d;
        }
    }
	
	private static String fromLongToString(long hash) {
        int length = (int) (hash & 0xf);
        if (length > 12 || length < 1)
            throw new IllegalArgumentException("invalid long geohash " + hash);
        char[] geohash = new char[length];
        for (int pos = 0; pos < length; pos++) {
            geohash[pos] = BASE32.charAt(((int) (hash >>> 59)));
            hash <<= 5;
        }
        return new String(geohash);
    }
	
	private static long encodeHashToLong(double latitude, double longitude, int length) {
        boolean isEven = true;
        double minLat = -90.0, maxLat = 90;
        double minLon = -180.0, maxLon = 180.0;
        long bit = 0x8000000000000000L;
        long g = 0;

        long target = 0x8000000000000000L >>> (5 * length);
        while (bit != target) {
            if (isEven) {
                double mid = (minLon + maxLon) / 2;
                if (longitude >= mid) {
                    g |= bit;
                    minLon = mid;
                } else
                    maxLon = mid;
            } else {
                double mid = (minLat + maxLat) / 2;
                if (latitude >= mid) {
                    g |= bit;
                    minLat = mid;
                } else
                    maxLat = mid;
            }

            isEven = !isEven;
            bit >>>= 1;
        }
        return g |= length;
    }

}
