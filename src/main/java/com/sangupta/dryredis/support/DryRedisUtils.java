package com.sangupta.dryredis.support;

import java.util.List;

import com.sangupta.dryredis.DryRedisGeo.GeoPoint;

public class DryRedisUtils {
	
	private static final double RADIUS_OF_EARTH = 6371; // in kilometers

	public static <V> List<V> subList(List<V> list, int start, int stop) {
		// TODO: fix this - remove problems with sublist
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
	
}
