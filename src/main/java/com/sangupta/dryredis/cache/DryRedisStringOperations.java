package com.sangupta.dryredis.cache;

import java.util.Collection;
import java.util.List;

public interface DryRedisStringOperations {

    int append(String key, String value);

    long incr(String key);

    long incrby(String key, long delta);

    double incrbyfloat(String key, double delta);

    List<String> mget(String[] keys);

    List<String> mget(Collection<String> keys);

    String set(String key, String value);

    String setnx(String key, String value);

    String setxx(String key, String value);

    String getrange(String key, int start, int end);

    int setrange(String key, int offset, String value);

    long bitcount(String key);

    long bitcount(String key, int start, int end);

    long decr(String key);

    long decrby(String key, long delta);

    String get(String key);

    int strlen(String key);

    String getset(String key, String value);

}
