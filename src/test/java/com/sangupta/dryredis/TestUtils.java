/**
 *
 * dry-redis: In-memory pure java implementation to Redis
 * Copyright (c) 2016, Sandeep Gupta
 * 
 * http://sangupta.com/projects/dry-redis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.sangupta.dryredis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestUtils {
    
    public static Set<String> asSet(String... str) {
        Set<String> set = new HashSet<String>();
        for(String s : str) {
            set.add(s);
        }
        
        return set;
    }

    public static List<String> asList(String... str) {
        List<String> list = new ArrayList<String>();
        
        if(str == null) {
            return list;
        }
        
        for(String s : str) {
            list.add(s);
        }
        
        return list;
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

    public static boolean equalUnsorted(List<String> list1, List<String> list2) {
        if(list1 == null || list2 == null) {
            return false;
        }
        
        for(String item : list2) {
            if(!list1.remove(item)) {
                return false;
            }
        }
        
        if(list1.isEmpty()) {
            return true;
        }
        
        return false;
    }
}
