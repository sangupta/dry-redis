package com.sangupta.dryredis.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sangupta.dryredis.cache.DryRedisSortedSetOperations;
import com.sangupta.dryredis.ds.ElementWithPriority;
import com.sangupta.dryredis.ds.SortedSetWithPriority;
import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;
import com.sangupta.dryredis.support.DryRedisRangeArgument;
import com.sangupta.dryredis.support.DryRedisSetAggregationType;

public class DryRedisSortedSet implements DryRedisCache, DryRedisSortedSetOperations {
    
    private Map<String, SortedSetWithPriority<String>> store = new HashMap<String, SortedSetWithPriority<String>>();
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zadd(java.lang.String, double, java.lang.String)
     */
    @Override
    public int zadd(String key, double score, String member) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            set = new SortedSetWithPriority<String>();
            this.store.put(key, set);
        }
        
        ElementWithPriority<String> element = new ElementWithPriority<String>(member, score);
        boolean added = set.add(element);
        if(added) {
            return 1;
        }
        
        return 0;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zcard(java.lang.String)
     */
    @Override
    public long zcard(String key) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        return set.size();
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zcount(java.lang.String, double, double)
     */
    @Override
    public long zcount(String key, double min, double max) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        long count = 0;
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            if(element.getPriority() >= min && element.getPriority() <= max) {
                count++;
            }
        }
        
        return count;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zincrby(java.lang.String, double, java.lang.String)
     */
    @Override
    public double zincrby(String key, double increment, String member) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            set = new SortedSetWithPriority<String>();
            this.store.put(key, set);
        }
        
        Double priority = set.getPriority(member);
        if(priority == null) {
            set.add(new ElementWithPriority<String>(member, increment));
            return increment;
        }
        
        // remove first
        ElementWithPriority<String> current = new ElementWithPriority<String>(member, priority);
        set.remove(current);
        
        // add again to resort
        double newScore = priority + increment;
        current = new ElementWithPriority<String>(member, newScore);
        set.add(current);
        
        return newScore;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zrank(java.lang.String, java.lang.String)
     */
    @Override
    public Integer zrank(String key, String member) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return null;
        }
        
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        int rank = 0;
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            if(element.getData().equals(member)) {
                return rank;
            }
            
            rank++;
        }
        
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zrevrank(java.lang.String, java.lang.String)
     */
    @Override
    public Integer zrevrank(String key, String member) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return null;
        }
        
        final int size = set.size();
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        int rank = 0;
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            if(element.getData().equals(member)) {
                return size - rank - 1; // reverse rank
            }
            
            rank++;
        }
        
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zrem(java.lang.String, java.lang.String)
     */
    @Override
    public int zrem(String key, String member) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            if(element.getData().equals(member)) {
                iterator.remove();
                return 1;
            }
        }
        
        return 0;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zrem(java.lang.String, java.util.Set)
     */
    @Override
    public int zrem(String key, Set<String> members) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        int removed = 0;
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            if(members.contains(element.getData())) {
                iterator.remove();
                removed++;
            }
        }
        
        return removed;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zscore(java.lang.String, java.lang.String)
     */
    @Override
    public Double zscore(String key, String member) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return null;
        }
        
        return set.getPriority(member);
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zrange(java.lang.String, int, int, boolean)
     */
    @Override
    public List<String> zrange(String key, int start, int stop, boolean withScores) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return new ArrayList<String>();
        }
        
        final int size = set.size();
        if(start < 0) {
            start = size + start;
        }
        if(stop < 0) {
            stop = size + stop;
        }
        
        // stop is inclusive
        stop++;
        
        if(stop > size) {
            stop = size;
        }
        
        List<String> result = new ArrayList<String>();
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        int index = 0;
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            
            if(index >= start && index <= stop) {
                result.add(element.getData());
                if(withScores) {
                    result.add(String.valueOf(element.getPriority()));
                }
            }
            
            // increment index at last
            index++;
        }
        
        return result;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zrangebylex(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<String> zrangebylex(String key, String min, String max) {
        return this.zrangebylex(key, new DryRedisRangeArgument(min), new DryRedisRangeArgument(max));
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zrangebylex(java.lang.String, com.sangupta.dryredis.support.DryRedisRangeArgument, com.sangupta.dryredis.support.DryRedisRangeArgument)
     */
    @Override
    public List<String> zrangebylex(String key, DryRedisRangeArgument min, DryRedisRangeArgument max) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return new ArrayList<String>();
        }
        
        List<String> result = new ArrayList<String>();
        
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            String value = element.getData();
            
            if(min.lessThan(value) && max.greaterThan(value)) {
                result.add(value);
            }
        }
        
        // must sort this result-set before returning
        Collections.sort(result);
        
        return result;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zrevrangebylex(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<String> zrevrangebylex(String key, String min, String max) {
        return this.zrevrangebylex(key, new DryRedisRangeArgument(max), new DryRedisRangeArgument(min));
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zrevrangebylex(java.lang.String, com.sangupta.dryredis.support.DryRedisRangeArgument, com.sangupta.dryredis.support.DryRedisRangeArgument)
     */
    @Override
    public List<String> zrevrangebylex(String key, DryRedisRangeArgument max, DryRedisRangeArgument min) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return new ArrayList<String>();
        }
        
        List<String> result = new ArrayList<String>();
        
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            String value = element.getData();
            
            if(min.lessThan(value) && max.greaterThan(value)) {
                result.add(value);
            }
        }
        
        // must sort this result-set before returning
        Collections.sort(result, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER));
        
        return result;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zlexcount(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public int zlexcount(String key, String min, String max) {
        return this.zlexcount(key, new DryRedisRangeArgument(min), new DryRedisRangeArgument(max));
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zlexcount(java.lang.String, com.sangupta.dryredis.support.DryRedisRangeArgument, com.sangupta.dryredis.support.DryRedisRangeArgument)
     */
    @Override
    public int zlexcount(String key, DryRedisRangeArgument min, DryRedisRangeArgument max) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        int count = 0;
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            String value = element.getData();
            
            if(min.lessThan(value) && max.greaterThan(value)) {
                count++;
            }
        }
        
        return count;
    }
    
//    // private helper methods
//    
//    private Element findInSet(SortedSet<Element> set, String member) {
//        if(set.isEmpty()) {
//            return null;
//        }
//        
//        Iterator<Element> iterator = set.iterator();
//        while(iterator.hasNext()) {
//            Element element = iterator.next();
//            if(element.value.equals(member)) {
//                return element;
//            }
//        }
//        
//        return null;
//    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zremrangebylex(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public int zremrangebylex(String key, String min, String max) {
        return this.zremrangebylex(key, new DryRedisRangeArgument(min), new DryRedisRangeArgument(max));
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zremrangebylex(java.lang.String, com.sangupta.dryredis.support.DryRedisRangeArgument, com.sangupta.dryredis.support.DryRedisRangeArgument)
     */
    @Override
    public int zremrangebylex(String key, DryRedisRangeArgument min, DryRedisRangeArgument max) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            String value = element.getData();
            
            if(min.lessThan(value) && max.greaterThan(value)) {
                iterator.remove();
                count++;
            }
        }
        
        return count;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zremrangebyrank(java.lang.String, int, int)
     */
    @Override
    public int zremrangebyrank(String key, int start, int stop) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        final int size = set.size();
        
        if(start < 0) {
            start = size + start;
        }
        if(stop < 0) {
            stop = size + stop;
        }
        
        // inclusive of stop
        stop++;
        
        // bounds
        if(stop > size) {
            stop = size;
        }
        
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        int rank = 0, removed = 0;
        while(iterator.hasNext()) {
            iterator.next(); // just move the pointer
            
            if(start <= rank && rank <= stop) {
                iterator.remove();
                removed++;
            }
            
            // increase rank at the end
            rank++;
        }
        
        return removed;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zremrangebyscore(java.lang.String, com.sangupta.dryredis.support.DryRedisRangeArgument, com.sangupta.dryredis.support.DryRedisRangeArgument)
     */
    @Override
    public int zremrangebyscore(String key, DryRedisRangeArgument min, DryRedisRangeArgument max) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        int removed = 0;
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            
            if(min.lessThan(element.getPriority()) && max.greaterThan(element.getPriority())) {
                iterator.remove();
                removed++;
            }
        }
        
        return removed;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zrevrange(java.lang.String, int, int, boolean)
     */
    @Override
    public List<String> zrevrange(String key, int start, int stop, boolean withScores) {
        SortedSetWithPriority<String> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return new ArrayList<String>();
        }
        
        final int size = set.size();
        if(start < 0) {
            start = size + start;
        }
        if(stop < 0) {
            stop = size + stop;
        }
        
        // stop is inclusive
        stop++;
        
        if(stop > size) {
            stop = size;
        }
        
        // TODO: fix this function for reverse ordering
        
        List<String> result = new ArrayList<String>();
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        int index = 0;
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            
            if(index >= start && index <= stop) {
                result.add(element.getData());
                if(withScores) {
                    result.add(String.valueOf(element.getPriority()));
                }
            }
            
            // increment index at last
            index++;
        }
        
        return result;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zinterstore(java.lang.String, java.util.List)
     */
    @Override
    public int zinterstore(String destination, List<String> keys) {
        return this.zinterstore(destination, keys, null, DryRedisSetAggregationType.SUM);
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zinterstore(java.lang.String, java.util.List, double[], com.sangupta.dryredis.support.DryRedisSetAggregationType)
     */
    @Override
    public int zinterstore(String destination, List<String> keys, double[] weights, DryRedisSetAggregationType aggregation) {
//        // just clone the same
//        SortedSetWithPriority<String> set = this.store.get(key);
//        if(newSet == null) {
//            newSet = new TreeSet<Element>();
//            this.store.put(destination, newSet);
//            return 0;
//        }
        
        // TODO: fix intersection
        return -1;
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zunionstore(java.lang.String, java.util.List)
     */
    @Override
    public int zunionstore(String destination, List<String> keys) {
        return this.zunionstore(destination, keys, null, DryRedisSetAggregationType.SUM);
    }
    
    /* (non-Javadoc)
     * @see com.sangupta.dryredis.cache.impl.DryRedisSortedSetOperations#zunionstore(java.lang.String, java.util.List, double[], com.sangupta.dryredis.support.DryRedisSetAggregationType)
     */
    @Override
    public int zunionstore(String destination, List<String> keys, double[] weights, DryRedisSetAggregationType aggregation) {
//        // just clone the same
//        SortedSetWithPriority<String> set = this.store.get(key);
//        if(newSet == null) {
//            newSet = new TreeSet<Element>();
//            this.store.put(destination, newSet);
//            return 0;
//        }
        
        // TODO: fix union
        return -1;
    }
    
    // from DryRedisCache interface

    @Override
    public int del(String key) {
        return 0;
    }

    @Override
    public DryRedisCacheType getType() {
        return DryRedisCacheType.SORTED_SET;
    }

    @Override
    public boolean hasKey(String key) {
        return false;
    }

    @Override
    public void keys(String pattern, List<String> keys) {
        
    }
    
}
