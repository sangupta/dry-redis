package com.sangupta.dryredis.support;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.dryredis.cache.impl.DryRedisGeo.GeoPoint;

public class TestDryRedisUtils {

    @Test
    public void testGetGeoDistance() {
        // only lat-change
        GeoPoint point1 = new GeoPoint("base", 28.545143900000003d, 77.3298117d);
        GeoPoint point2 = new GeoPoint("destination", 48.545143900000003d, 77.3298117d);
        
        double distance = DryRedisUtils.getGeoDistance(point1, point2);
        Assert.assertEquals(2223.898d, distance, 0.1d);
        
        // only long-change
        point1 = new GeoPoint("base", 28.545143900000003d, 77.3298117d);
        point2 = new GeoPoint("destination", 28.545143900000003d, 57.3298117d);
        
        distance = DryRedisUtils.getGeoDistance(point1, point2);
        Assert.assertEquals(1951.278d, distance, 0.1d);
        
        // both lat-long
        point1 = new GeoPoint("base", 28.545143900000003d, 77.3298117d);
        point2 = new GeoPoint("destination", 48.545143900000003d, 57.3298117d);
        
        distance = DryRedisUtils.getGeoDistance(point1, point2);
        Assert.assertEquals(2805.199d, distance, 0.1d);
    }
    
    @Test
    public void testDegreeToRadians() {
        Assert.assertEquals(0d, DryRedisUtils.degreeToRadians(0), 0d);
        Assert.assertEquals(Math.PI, DryRedisUtils.degreeToRadians(180), 0d);
        Assert.assertEquals(1.570796326d, DryRedisUtils.degreeToRadians(90), 0.00001d);
    }
    
}
