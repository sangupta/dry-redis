package com.sangupta.dryredis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestDryRedisUtils {
    
    public static Set<String> asSet(String... str) {
        Set<String> set = new HashSet<String>();
        for(String s : str) {
            set.add(s);
        }
        
        return set;
    }

    public static boolean contains(Set<String> set, String str) {
        if(set == null || set.isEmpty()) {
            return false;
        }
        
        return set.contains(str);
    }

    public static boolean contains(Set<String> set, List<String> values) {
        if(set == null || set.isEmpty()) {
            return false;
        }
        
        if(values == null || values.isEmpty()) {
            return false;
        }
        
        for(String s : values) {
            if(!set.contains(s)) {
                return false;
            }
        }
        
        return true;
    }

}
