package com.sangupta.dryredis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sangupta.dryredis.cache.DryRedisGeoOperations;
import com.sangupta.dryredis.cache.DryRedisHashOperations;
import com.sangupta.dryredis.cache.DryRedisStringOperations;
import com.sangupta.dryredis.support.DryRedisCacheType;
import com.sangupta.dryredis.support.DryRedisGeoUnit;
import com.sangupta.dryredis.support.DryRedisInsertOrder;

public class DryRedis extends DryRedisKeys implements DryRedisGeoOperations, DryRedisHashOperations, DryRedisStringOperations {
    
    // GEO commands follow

    public void geoadd(String key, double latitude, double longitude, String member) {
        matchKeyType(key, DryRedisCacheType.GEO);
        this.geoCommands.geoadd(key, latitude, longitude, member);
    }
    
    public String geohash(String key, String member) {
        matchKeyType(key, DryRedisCacheType.GEO);
        return this.geoCommands.geohash(key, member);
    }
    
    public double[] geopos(String key, String member) {
        matchKeyType(key, DryRedisCacheType.GEO);
        return this.geoCommands.geopos(key, member);
    }
    
    public Double geodist(String key, String member1, String member2, DryRedisGeoUnit unit) {
        matchKeyType(key, DryRedisCacheType.GEO);
        return this.geoCommands.geodist(key, member1, member2, unit);
    }
    
    public List<String> georadius(String key, double latitude, double longitude, double radius, DryRedisGeoUnit unit) {
        matchKeyType(key, DryRedisCacheType.GEO);
        return this.geoCommands.georadius(key, latitude, longitude, radius, unit);
    }
    
    public List<String> georadius(String key, double latitude, double longitude, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count) {
        matchKeyType(key, DryRedisCacheType.GEO);
        return this.geoCommands.georadius(key, latitude, longitude, radius, unit, withCoordinates, withDistance, withHash, count);
    }
    
    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit) {
        matchKeyType(key, DryRedisCacheType.GEO);
        return this.geoCommands.georadiusbymember(key, member, radius, unit);
    }
    
    public List<String> georadiusbymember(String key, String member, double radius, DryRedisGeoUnit unit, boolean withCoordinates, boolean withDistance, boolean withHash, int count) {
        matchKeyType(key, DryRedisCacheType.GEO);
        return this.geoCommands.georadiusbymember(key, member, radius, unit, withCoordinates, withDistance, withHash, count);
    }

    // HASH commands follow
    
    public int hdel(String key, String field) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hdel(key, field);
    }
    
    public int hdel(String key, List<String> fields) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hdel(key, fields);
    }
    
    public int hexists(String key, String field) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hexists(key, field);
    }
    
    public Object hget(String key, String field) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hget(key, field);
    }
    
    public List<String> hgetall(String key) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hgetall(key);
    }
    
    public List<String> hkeys(String key) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hkeys(key);
    }
    
    public int hlen(String key) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hlen(key);
    }
    
    public int hset(String key, String field, String value) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hset(key, field, value);
    }
    
    public int hsetnx(String key, String field, String value) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hsetnx(key, field, value);
    }
    
    public int hstrlen(String key, String field) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hstrlen(key, field);
    }
    
    public List<String> hvals(String key) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hvals(key);
    }
    
    public long hincrby(String key, String field, long increment) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hincrby(key, field, increment);
    }
    
    public double hincrbyfloat(String key, String field, double increment) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hincrbyfloat(key, field, increment);
    }
    
    public List<String> hmget(String key, List<String> fields) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hmget(key, fields);
    }
    
    public String hmset(String key, Map<String, String> fieldValues) {
        matchKeyType(key, DryRedisCacheType.HASH);
        return this.hashCommands.hmset(key, fieldValues);
    }
    
    // HYPERLOGLOG commands
    
    public int pfadd(String key) {
        matchKeyType(key, DryRedisCacheType.HYPER_LOG_LOG);
        return this.hyperLogLogCommands.pfadd(key);
    }
    
    public int pfadd(String key, String element) {
        matchKeyType(key, DryRedisCacheType.HYPER_LOG_LOG);
        return this.hyperLogLogCommands.pfadd(key, element);
    }
    
    public long pfcount(String key) {
        matchKeyType(key, DryRedisCacheType.HYPER_LOG_LOG);
        return this.hyperLogLogCommands.pfcount(key);
    }
    
    public long pfcount(List<String> keys) {
        for(String key : keys) {
            matchKeyType(key, DryRedisCacheType.HYPER_LOG_LOG);
        }
        
        return this.hyperLogLogCommands.pfcount(keys);
    }
    
    public String pfmerge(String destination, List<String> keys) {
        for(String key : keys) {
            matchKeyType(key, DryRedisCacheType.HYPER_LOG_LOG);
        }

        return this.hyperLogLogCommands.pfmerge(destination, keys);
    }

    // LIST commands
    
    public String blpop(String key, int maxSecondsToBlock) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.blpop(key, maxSecondsToBlock);
    }
    
    public String brpop(String key, int maxSecondsToBlock) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.brpop(key, maxSecondsToBlock);
    }
    
    public String brpoplpush(String source, String destination, int maxSecondsToBlock) {
        matchKeyType(source, DryRedisCacheType.LIST);
        matchKeyType(destination, DryRedisCacheType.LIST);
        return this.brpoplpush(source, destination, maxSecondsToBlock);
    }
    
    public String lindex(String key, int index) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.lindex(key, index);
    }
    
    public int linsert(String key, DryRedisInsertOrder order, String pivot, String value) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.linsert(key, order, pivot, value);
    }
    
    public int llen(String key) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.llen(key);
    }
    
    public String lpop(String key) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.lpop(key);
    }
    
    public int lpush(String key, String value) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.lpush(key, value);
    }
    
    public int lpush(String key, List<String> values) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.lpush(key, values);
    }
    
    public int lpushx(String key, List<String> values) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.lpushx(key, values);
    }
    
    public List<String> lrange(String key, int start, int stop) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.lrange(key, start, stop);
    }
    
    public int lrem(String key, int count, String value) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.lrem(key, count, value);
    }
    
    public String lset(String key, int index, String value) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.lset(key, index, value);
    }
    
    public void ltrim(String key, int start, int stop) {
        matchKeyType(key, DryRedisCacheType.LIST);
        this.listCommands.ltrim(key, start, stop);
    }
    
    public String rpop(String key) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.rpop(key);
    }
    
    public String rpoplpush(String source, String destination) {
        matchKeyType(source, DryRedisCacheType.LIST);
        matchKeyType(destination, DryRedisCacheType.LIST);
        return this.listCommands.rpoplpush(source, destination);
    }
    
    public int rpush(String key, String value) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.rpush(key, value);
    }
    
    public int rpushx(String key, String value) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.rpushx(key, value);
    }
    
    public int rpush(String key, List<String> values) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.rpush(key, values);
    }
    
    public int lpushx(String key, String value) {
        matchKeyType(key, DryRedisCacheType.LIST);
        return this.listCommands.lpushx(key, value);
    }
    
    // SET commands
    
    public int sadd(String key, String value) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.sadd(key, value);
    }
    
    public int sadd(String key, List<String> values) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.sadd(key, values);
    }
    
    public int scard(String key) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.scard(key);
    }
    
    public Set<String> sdiff(String key, String... otherKeys) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.sdiff(key, otherKeys);
    }
    
    public int sdiffstore(String destination, String key, String... otherKeys) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.sdiffstore(destination, key, otherKeys);
    }
    
    public Set<String> sinter(String key, String... otherKeys) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.sinter(key, otherKeys);
    }
    
    public int sinterstore(String destination, String key, String... otherKeys) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.sinterstore(destination, key, otherKeys);
    }
    
    public int sismember(String key, String value) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.sismember(key, value);
    }
    
    public Set<String> smembers(String key) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.smembers(key);
    }
    
    public int smove(String source, String destination, String value) {
        matchKeyType(source, DryRedisCacheType.SET);
        matchKeyType(destination, DryRedisCacheType.SET);
        return this.setCommands.smove(source, destination, value);
    }
    
    public String spop(String key) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.spop(key);
    }
    
    public List<String> spop(String key, int count) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.spop(key, count);
    }
    
    public List<String> srandmember(String key, int count) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.srandmember(key, count);
    }
    
    public int srem(String key, String value) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.srem(key, value);
    }
    
    public int srem(String key, List<String> values) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.srem(key, values);
    }
    
    public Set<String> sunion(String key, String... otherKeys) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.sunion(key, otherKeys);
    }
    
    public int sunionstore(String destination, String key, String... otherKeys) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.sunionstore(destination, key, otherKeys);
    }
    
    public List<String> sscan(String key, int cursor) {
        matchKeyType(key, DryRedisCacheType.SET);
        return this.setCommands.sscan(key, cursor);
    }
    
    // SORTED-SET commands
    
    // STRING commands

    public int append(String key, String value) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.append(key, value);
    }
    
    public long incr(String key) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.incr(key);
    }
    
    public long incrby(String key, long delta) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.incrby(key, delta);
    }
    
    public double incrbyfloat(String key, double delta) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.incrbyfloat(key, delta);
    }
    
    public List<String> mget(String[] keys) {
        for(String key : keys) {
            matchKeyType(key, DryRedisCacheType.STRING);
        }
        
        return this.stringCommands.mget(keys);
    }
    
    public List<String> mget(Collection<String> keys) {
        for(String key : keys) {
            matchKeyType(key, DryRedisCacheType.STRING);
        }
        
        return this.stringCommands.mget(keys);
    }
    
    public String set(String key, String value) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.set(key, value);
    }
    
    public String setnx(String key, String value) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.setnx(key, value);
    }
    
    public String setxx(String key, String value) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.setxx(key, value);
    }
    
    public String getrange(String key, int start, int end) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.getrange(key, start, end);
    }
    
    public int setrange(String key, int offset, String value) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.setrange(key, offset, value);
    }
    
    public long bitcount(String key, int start, int end) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.bitcount(key, start, end);
    }
    
    public long decr(String key) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.decr(key);
    }
    
    public long decrby(String key, long delta) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.decrby(key, delta);
    }
    
    public String get(String key) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.get(key);
    }
    
    public int strlen(String key) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.strlen(key);
    }
    
    public String getset(String key, String value) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.getset(key, value);
    }
    
    public long bitcount(String key) {
        matchKeyType(key, DryRedisCacheType.STRING);
        return this.stringCommands.bitcount(key);
    }

}
