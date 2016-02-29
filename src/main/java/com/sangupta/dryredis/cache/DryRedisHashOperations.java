package com.sangupta.dryredis.cache;

import java.util.List;
import java.util.Map;

public interface DryRedisHashOperations {

    public int hdel(String key, String field);

    public int hdel(String key, List<String> fields);

    public int hexists(String key, String field);

    public Object hget(String key, String field);

    public List<String> hgetall(String key);

    public List<String> hkeys(String key);

    public int hlen(String key);

    public int hset(String key, String field, String value);

    public int hsetnx(String key, String field, String value);

    public int hstrlen(String key, String field);

    public List<String> hvals(String key);

    public long hincrby(String key, String field, long increment);

    public double hincrbyfloat(String key, String field, double increment);

    public List<String> hmget(String key, List<String> fields);

    public String hmset(String key, Map<String, String> fieldValues);

}