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

public class RedisGeoHash {

    private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";
    
    private static class GeoHashRange {
        
        final double min;
        
        final double max;
        
        GeoHashRange(double max, double min) {
            this.max = max;
            this.min = min;
        }
        
    }
    
    private static final double GEO_LAT_MIN = -85.05112878d;
    private static final double GEO_LAT_MAX = 85.05112878d;
    private static final double GEO_LONG_MIN = -180d;
    private static final double GEO_LONG_MAX = 180d;
    
    private static final GeoHashRange longRange = new GeoHashRange(GEO_LONG_MAX, GEO_LONG_MIN);
    
    private static final GeoHashRange latRange = new GeoHashRange(GEO_LAT_MAX, GEO_LAT_MIN);
    
    public static void main(String[] args) {
        System.out.println(geoHash(38.115556395496299d, 13.361389338970184d));
    }
    
    public static String geoHash(double latitude, double longitude) {
        String hashS = null;
        for(int index = 0; index < 32; index++) {
            long hash = geoHashEncode(longRange, latRange, longitude, latitude, 5);
            hashS = fromLongToString(hash);
            System.out.println(index + ": " + hash);
        }
        
        return hashS;
    }
    
    /**
     * Code borrowed from <code>geoHashEncode</code> method from https://github.com/antirez/redis/blob/unstable/deps/geohash-int/geohash.c
     * 
     * @param longRange
     * @param latRange
     * @param longitude
     * @param latitude
     * @param step
     * @return
     */
    private static long geoHashEncode(GeoHashRange longRange, GeoHashRange latRange, double longitude, double latitude, int step) {
        if(step > 32 || step == 0) {
            return 0;
        }
        
        if(rangepIsZero(latRange) || rangepIsZero(longRange)) {
            return 0;
        }
        
        if (longitude > 180d || longitude < -180d || latitude > 85.05112878d || latitude < -85.05112878d) {
            return 0;
        }
        
        if (latitude < latRange.min || latitude > latRange.max || longitude < longRange.min || longitude > longRange.max) {
            return 0;
        }
        
        double lat_offset = (latitude - latRange.min) / (latRange.max - latRange.min);
        double long_offset = (longitude - longRange.min) / (longRange.max - longRange.min);
        
        lat_offset *= (1 << step);
        long_offset *= (1 << step);
        
        return interLeave64(lat_offset, long_offset);
    }
    
    private static boolean rangepIsZero(GeoHashRange range) {
        if(range == null) {
            return true;
        }
        
        if(range.min == 0 && range.max == 0) {
            return true;
        }
        
        return false;
    }

    private static final long[] B = { 0x5555555555555555l, 0x3333333333333333l, 0x0F0F0F0F0F0F0F0Fl, 0x00FF00FF00FF00FFl, 0x0000FFFF0000FFFFl};
    
    private static final int[] S = {1, 2, 4, 8, 16};
    
    private static long interLeave64(double xlo, double ylo) {
        long x = Double.valueOf(xlo).intValue();
        long y = Double.valueOf(ylo).intValue();
        
        x = (x | (x << S[4])) & B[4];
        y = (y | (y << S[4])) & B[4];

        x = (x | (x << S[3])) & B[3];
        y = (y | (y << S[3])) & B[3];

        x = (x | (x << S[2])) & B[2];
        y = (y | (y << S[2])) & B[2];

        x = (x | (x << S[1])) & B[1];
        y = (y | (y << S[1])) & B[1];

        x = (x | (x << S[0])) & B[0];
        y = (y | (y << S[0])) & B[0];

        return x | (y << 1);
    }

    public static String hash(double latitude, double longitude) {
        return encodeHash(latitude, longitude, 210);
    }
    
    public static String encodeHash(double latitude, double longitude, int length) {
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
        if (length > 12 || length < 1) {
            throw new IllegalArgumentException("invalid long geohash " + hash);
        }
        
        char[] geohash = new char[length];
        for (int pos = 0; pos < length; pos++) {
            geohash[pos] = BASE32.charAt(((int) (hash >>> 59)));
            hash <<= 5;
        }
        return new String(geohash);
    }
    
    private static long encodeHashToLong(double latitude, double longitude, int length) {
        boolean isEven = true;
        double minLat = GEO_LAT_MIN, maxLat = GEO_LAT_MAX;
        double minLon = GEO_LONG_MIN, maxLon = GEO_LONG_MAX;
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
