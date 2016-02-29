package com.sangupta.dryredis.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;
import com.sangupta.dryredis.support.DryRedisRangeArgument;

public class DryRedisSortedSet implements DryRedisCache {
    
    private Map<String, SortedSet<Element>> store = new HashMap<String, SortedSet<Element>>(); 
    
    public int zadd(String key, double score, String member) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            set = new TreeSet<Element>();
            this.store.put(key, set);
        }
        
        Element element = new Element(member, score);
        boolean added = set.add(element);
        if(added) {
            return 1;
        }
        
        return 0;
    }
    
    public long zcard(String key) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        return set.size();
    }
    
    public long zcount(String key, double min, double max) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        Iterator<Element> iterator = set.iterator();
        long count = 0;
        while(iterator.hasNext()) {
            Element element = iterator.next();
            if(element.score >= min && element.score <= max) {
                count++;
            }
        }
        
        return count;
    }
    
    public double zincrby(String key, double increment, String member) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            set = new TreeSet<Element>();
            this.store.put(key, set);
        }
        
        Element current = findInSet(set, member);
        if(current == null) {
            current = new Element(member, increment);
            set.add(current);
            return increment;
        }
        
        set.remove(current);
        double newScore = current.score + increment;
        current = new Element(member, newScore);
        set.add(current);
        
        return newScore;
    }
    
    public Integer zrank(String key, String member) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return null;
        }
        
        Iterator<Element> iterator = set.iterator();
        int rank = 0;
        while(iterator.hasNext()) {
            Element element = iterator.next();
            if(element.value.equals(member)) {
                return rank;
            }
            
            rank++;
        }
        
        return null;
    }
    
    public Integer zrevrank(String key, String member) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return null;
        }
        
        final int size = set.size();
        Iterator<Element> iterator = set.iterator();
        int rank = 0;
        while(iterator.hasNext()) {
            Element element = iterator.next();
            if(element.value.equals(member)) {
                return size - rank - 1; // reverse rank
            }
            
            rank++;
        }
        
        return null;
    }
    
    public int zrem(String key, String member) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        Iterator<Element> iterator = set.iterator();
        while(iterator.hasNext()) {
            Element element = iterator.next();
            if(element.value.equals(member)) {
                iterator.remove();
                return 1;
            }
        }
        
        return 0;
    }
    
    public int zrem(String key, Set<String> members) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        Iterator<Element> iterator = set.iterator();
        int removed = 0;
        while(iterator.hasNext()) {
            Element element = iterator.next();
            if(members.contains(element.value)) {
                iterator.remove();
                removed++;
            }
        }
        
        return removed;
    }
    
    public Double zscore(String key, String member) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return null;
        }
        
        Element element = this.findInSet(set, member);
        if(element == null) {
            return null;
        }
        
        return element.score;
    }
    
    public List<String> zrange(String key, int start, int stop, boolean withScores) {
        SortedSet<Element> set = this.store.get(key);
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
        Iterator<Element> iterator = set.iterator();
        int index = 0;
        while(iterator.hasNext()) {
            Element element = iterator.next();
            
            if(index >= start && index <= stop) {
                result.add(element.value);
                if(withScores) {
                    result.add(String.valueOf(element.score));
                }
            }
            
            // increment index at last
            index++;
        }
        
        return result;
    }
    
    public List<String> zrangebylex(String key, String min, String max) {
        return this.zrangebylex(key, new DryRedisRangeArgument(min), new DryRedisRangeArgument(max));
    }
    
    public List<String> zrangebylex(String key, DryRedisRangeArgument min, DryRedisRangeArgument max) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return new ArrayList<String>();
        }
        
        List<String> result = new ArrayList<String>();
        
        Iterator<Element> iterator = set.iterator();
        while(iterator.hasNext()) {
            Element element = iterator.next();
            String value = element.value;
            
            if(min.lessThan(value) && max.greaterThan(value)) {
                result.add(value);
            }
        }
        
        // must sort this result-set before returning
        Collections.sort(result);
        
        return result;
    }
    
    public List<String> zrevrangebylex(String key, String min, String max) {
        return this.zrevrangebylex(key, new DryRedisRangeArgument(max), new DryRedisRangeArgument(min));
    }
    
    public List<String> zrevrangebylex(String key, DryRedisRangeArgument max, DryRedisRangeArgument min) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return null;
        }
        
        if(set.isEmpty()) {
            return new ArrayList<String>();
        }
        
        List<String> result = new ArrayList<String>();
        
        Iterator<Element> iterator = set.iterator();
        while(iterator.hasNext()) {
            Element element = iterator.next();
            String value = element.value;
            
            if(min.lessThan(value) && max.greaterThan(value)) {
                result.add(value);
            }
        }
        
        // must sort this result-set before returning
        Collections.sort(result, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER));
        
        return result;
    }
    
    public int zlexcount(String key, String min, String max) {
        return this.zlexcount(key, new DryRedisRangeArgument(min), new DryRedisRangeArgument(max));
    }
    
    public int zlexcount(String key, DryRedisRangeArgument min, DryRedisRangeArgument max) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        Iterator<Element> iterator = set.iterator();
        int count = 0;
        while(iterator.hasNext()) {
            Element element = iterator.next();
            String value = element.value;
            
            if(min.lessThan(value) && max.greaterThan(value)) {
                count++;
            }
        }
        
        return count;
    }
    
    // private helper methods
    
    private Element findInSet(SortedSet<Element> set, String member) {
        if(set.isEmpty()) {
            return null;
        }
        
        Iterator<Element> iterator = set.iterator();
        while(iterator.hasNext()) {
            Element element = iterator.next();
            if(element.value.equals(member)) {
                return element;
            }
        }
        
        return null;
    }
    
    public int zremrangebylex(String key, String min, String max) {
        return this.zremrangebylex(key, new DryRedisRangeArgument(min), new DryRedisRangeArgument(max));
    }
    
    public int zremrangebylex(String key, DryRedisRangeArgument min, DryRedisRangeArgument max) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        
        Iterator<Element> iterator = set.iterator();
        while(iterator.hasNext()) {
            Element element = iterator.next();
            String value = element.value;
            
            if(min.lessThan(value) && max.greaterThan(value)) {
                iterator.remove();
                count++;
            }
        }
        
        return count;
    }
    
    public int zremrangebyrank(String key, int start, int stop) {
        SortedSet<Element> set = this.store.get(key);
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
        
        Iterator<Element> iterator = set.iterator();
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
    
    public int zremrangebyscore(String key, DryRedisRangeArgument min, DryRedisRangeArgument max) {
        SortedSet<Element> set = this.store.get(key);
        if(set == null) {
            return 0;
        }
        
        if(set.isEmpty()) {
            return 0;
        }
        
        Iterator<Element> iterator = set.iterator();
        int removed = 0;
        while(iterator.hasNext()) {
            Element element = iterator.next();
            
            if(min.lessThan(element.score) && max.greaterThan(element.score)) {
                iterator.remove();
                removed++;
            }
        }
        
        return removed;
    }
    
    public List<String> zrevrange(String key, int start, int stop, boolean withScores) {
        SortedSet<Element> set = this.store.get(key);
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
        Iterator<Element> iterator = set.iterator();
        int index = 0;
        while(iterator.hasNext()) {
            Element element = iterator.next();
            
            if(index >= start && index <= stop) {
                result.add(element.value);
                if(withScores) {
                    result.add(String.valueOf(element.score));
                }
            }
            
            // increment index at last
            index++;
        }
        
        return result;
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
    
    // static class that holds each element
    
    private static class Element implements Comparable<Element> {
        
        String value;
        
        double score;
        
        public Element(String value, double score) {
            this.value = value;
            this.score = score;
        }
        
        @Override
        public int hashCode() {
            if(value == null) {
                return 0;
            }
            
            return this.value.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            
            if(this == obj) {
                return true;
            }
            
            if(!(obj instanceof Element)) {
                return false;
            }
            
            if(this.value == null) {
                return false;
            }
            
            return this.value.equals(((Element) obj).value);
        }

        @Override
        public int compareTo(Element o) {
            if(o == null) {
                return -1;
            }
            
            if(this == o) {
                return 0;
            }
            
            int compare = Double.compare(this.score, o.score);
            if(compare == 0) {
                return this.value.compareTo(o.value);
            }
            
            return compare;
        }
        
    }
    
    private static class ScoreComparator implements Comparator<Element> {
        
        private static final ScoreComparator INSTANCE = new ScoreComparator(); 

        @Override
        public int compare(Element o1, Element o2) {
            if(o1 == null) {
                return -1;
            }
            
            if(o2 == null) {
                return 1;
            }
            
            if(o1 == o2) {
                return 0;
            }
            
            return o1.compareTo(o2);
        }
        
    }
}
