package com.sangupta.dryredis.support;

import org.junit.Assert;
import org.junit.Test;

/**
 * unit tests for {@link DryRedisGeoPoint} class.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisGeoPoint {

    @Test
    public void test() {
        DryRedisGeoPoint point1 = new DryRedisGeoPoint("someplace", 23d, 21d);
        
        Assert.assertNotNull(point1);
        
        double[] values = point1.getPoint();
        Assert.assertNotNull(values);
        Assert.assertEquals(2, values.length);
        Assert.assertEquals(23d, values[0], 0d);
        Assert.assertEquals(21d, values[1], 0d);
        
        // tostring
        Assert.assertNotNull(point1.toString());
        
        // geo hash
        Assert.assertNotNull(point1.getGeoHash());
        
        // hashcode and equals
        DryRedisGeoPoint point2 = new DryRedisGeoPoint("someplace2", 23d, 21d);
        
        Assert.assertEquals(point1.hashCode(), point2.hashCode());
        Assert.assertEquals(point1, point2);
        
        // basic equals testing
        Assert.assertFalse(point1.equals(null));
        Assert.assertTrue(point1.equals(point1));
        Assert.assertFalse(point1.equals(DryRedisCacheType.LIST));
    }
}
