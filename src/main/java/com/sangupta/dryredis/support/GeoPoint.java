package com.sangupta.dryredis.support;

import com.sangupta.dryredis.support.RedisGeoHash;

/**
 * Defines a Geographical point that has a latitude and a longitude.
 * 
 * @author sangupta
 *
 */
public class GeoPoint {
    
    public static final int THOUSAND = 1000;

    public static final int MILLION = THOUSAND * THOUSAND;
    
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
        
        if(!(obj instanceof GeoPoint)) {
            return false;
        }
        
        GeoPoint gp = (GeoPoint) obj;
        return this.latitude == gp.latitude && this.longitude == gp.longitude;
    }
}
