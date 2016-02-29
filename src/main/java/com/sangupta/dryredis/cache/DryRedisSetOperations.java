package com.sangupta.dryredis.cache;

import java.util.List;
import java.util.Set;

public interface DryRedisSetOperations {

    int sadd(String key, String value);

    int sadd(String key, List<String> values);

    int scard(String key);

    Set<String> sdiff(String key, String... otherKeys);

    int sdiffstore(String destination, String key, String... otherKeys);

    Set<String> sinter(String key, String... otherKeys);

    int sinterstore(String destination, String key, String... otherKeys);

    int sismember(String key, String value);

    Set<String> smembers(String key);

    int smove(String source, String destination, String value);

    String spop(String key);

    List<String> spop(String key, int count);

    String srandmember(String key);

    List<String> srandmember(String key, int count);

    int srem(String key, String value);

    int srem(String key, List<String> values);

    Set<String> sunion(String key, String... otherKeys);

    int sunionstore(String destination, String key, String... otherKeys);

    List<String> sscan(String key, int cursor);

}