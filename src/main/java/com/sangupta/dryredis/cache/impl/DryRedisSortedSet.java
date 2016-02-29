package com.sangupta.dryredis.cache.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sangupta.dryredis.support.DryRedisCache;
import com.sangupta.dryredis.support.DryRedisCacheType;

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
            
            return Double.compare(this.score, o.score);
        }
        
    }
}
