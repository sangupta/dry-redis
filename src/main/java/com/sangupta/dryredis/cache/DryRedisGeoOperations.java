package com.sangupta.dryredis.cache;

import java.util.List;

import com.sangupta.dryredis.support.DryRedisGeoUnit;

public interface DryRedisGeoOperations {

    public void geoadd(String key, double latitude, double longitude, String member);

    public String geohash(String key, String member);

    public double[] geopos(String key, String member);

    public Double geodist(String key, String member1, String member2, DryRedisGeoUnit unit);

    public List<String> georadius(String key, double latitude, double longitude, double radius, DryRedisGeoUnit unit);

    public List<String> georadius(String key, double latitude, double longitude, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count);

    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit);

    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count);

}