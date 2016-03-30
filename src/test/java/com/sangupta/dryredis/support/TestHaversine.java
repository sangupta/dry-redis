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
