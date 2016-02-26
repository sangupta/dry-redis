package com.sangupta.dryredis;

import java.util.List;
import java.util.Map;

import com.sangupta.dryredis.support.DryRedisCacheType;
import com.sangupta.dryredis.support.DryRedisGeoUnit;

public class DryRedis extends DryRedisKeys {
    
    // GEO commands follow

    public void geoadd(String key, double latitude, double longitude, String member) {
        matchKeyType(key, DryRedisCacheType.Geo);
        this.geo.geoadd(key, latitude, longitude, member);
    }
    
    public String geohash(String key, String member) {
        matchKeyType(key, DryRedisCacheType.Geo);
        return this.geo.geohash(key, member);
    }
    
    public double[] geopos(String key, String member) {
        matchKeyType(key, DryRedisCacheType.Geo);
        return this.geo.geopos(key, member);
    }
    
    public Double geodist(String key, String member1, String member2, DryRedisGeoUnit unit) {
        matchKeyType(key, DryRedisCacheType.Geo);
        return this.geo.geodist(key, member1, member2, unit);
    }
    
    public List<String> georadius(String key, double latitude, double longitude, double radius, DryRedisGeoUnit unit) {
        matchKeyType(key, DryRedisCacheType.Geo);
        return this.geo.georadius(key, latitude, longitude, radius, unit);
    }
    
    public List<String> georadius(String key, double latitude, double longitude, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count) {
        matchKeyType(key, DryRedisCacheType.Geo);
        return this.geo.georadius(key, latitude, longitude, radius, unit, withCoordinates, withDistance, withHash, count);
    }
    
    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit) {
        matchKeyType(key, DryRedisCacheType.Geo);
        return this.geo.georadiusbymember(key, member, radius, unit);
    }
    
    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count) {
        matchKeyType(key, DryRedisCacheType.Geo);
        return this.geo.georadiusbymember(key, member, radius, unit, withCoordinates, withDistance, withHash, count);
    }

    // HASH commands follow
    
    public int hdel(String key, String field) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hdel(key, field);
    }
    
    public int hdel(String key, List<String> fields) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hdel(key, fields);
    }
    
    public int hexists(String key, String field) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hexists(key, field);
    }
    
    public Object hget(String key, String field) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hget(key, field);
    }
    
    public List<String> hgetall(String key) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hgetall(key);
    }
    
    public List<String> hkeys(String key) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hkeys(key);
    }
    
    public int hlen(String key) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hlen(key);
    }
    
    public int hset(String key, String field, String value) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hset(key, field, value);
    }
    
    public int hsetnx(String key, String field, String value) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hsetnx(key, field, value);
    }
    
    public int hstrlen(String key, String field) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hstrlen(key, field);
    }
    
    public List<String> hvals(String key) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hvals(key);
    }
    
    public long hincrby(String key, String field, long increment) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hincrby(key, field, increment);
    }
    
    public double hincrbyfloat(String key, String field, double increment) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hincrbyfloat(key, field, increment);
    }
    
    public List<String> hmget(String key, List<String> fields) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hmget(key, fields);
    }
    
    public String hmset(String key, Map<String, String> fieldValues) {
        matchKeyType(key, DryRedisCacheType.Hash);
        return this.hash.hmset(key, fieldValues);
    }
    
    // HYPERLOGLOG commands
    
    public int pfadd(String key) {
        matchKeyType(key, DryRedisCacheType.HyperLogLog);
        return this.hyperLogLog.pfadd(key);
    }
    
    public int pfadd(String key, String element) {
        matchKeyType(key, DryRedisCacheType.HyperLogLog);
        return this.hyperLogLog.pfadd(key, element);
    }
    
    public long pfcount(String key) {
        matchKeyType(key, DryRedisCacheType.HyperLogLog);
        return this.hyperLogLog.pfcount(key);
    }
    
    public long pfcount(List<String> keys) {
        for(String key : keys) {
            matchKeyType(key, DryRedisCacheType.HyperLogLog);
        }
        
        return this.hyperLogLog.pfcount(keys);
    }
    
    public String pfmerge(String destination, List<String> keys) {
        for(String key : keys) {
            matchKeyType(key, DryRedisCacheType.HyperLogLog);
        }

        return this.hyperLogLog.pfmerge(destination, keys);
    }

    // LIST commands
    
    // SET commands
    
    // SORTED-SET commands
    
    // STRING commands
    
}
