package com.sangupta.dryredis.cache;

import java.util.List;

import com.sangupta.dryredis.support.DryRedisGeoUnit;

public interface DryRedisGeoOperations {

    public int geoadd(String key, double longitude, double latitude, String member);

    public String geohash(String key, String member);

    public double[] geopos(String key, String member);

    public Double geodist(String key, String member1, String member2, DryRedisGeoUnit unit);

    public List<String> georadius(String key, double longitude, double latitude, double radius, DryRedisGeoUnit unit);

    public List<String> georadius(String key, double longitude, double latitude, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count);

    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit);

    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count);

}
