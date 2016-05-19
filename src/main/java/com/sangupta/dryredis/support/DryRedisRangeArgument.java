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

package com.sangupta.dryredis.support;

/**
 * Value object that holds parameters for range arguments for
 * REDIS SortedSet commands.
 * 
 * @author sangupta
 *
 */
public class DryRedisRangeArgument {
    
    private final boolean infinity;
    
    private final boolean inclusive;
    
    private final String value;
    
    public DryRedisRangeArgument(String value) {
        if(value.equals("-")) {
            this.inclusive = true;
            this.infinity = true;
            this.value = value;
            return;
        }
        
        if(value.equals("+")) {
            this.inclusive = true;
            this.infinity = true;
            this.value = value;
            return;
        }
        
        this.infinity = false;
        
        if(value.startsWith("(") || value.startsWith("[")) {
            this.value = value.substring(1);
        } else {
            this.value = value;
        }
        
        this.inclusive = value.startsWith("[");
    }
    
    public boolean lessThan(String s) {
        if(this.infinity) {
            if("-".equals(this.value)) {
                return true;
            }
            
            return false;
        }
        
        int compare = this.value.compareTo(s);
        if(this.inclusive) {
            return compare <= 0;
        }
        
        return compare < 0;
    }
    
    public boolean lessThan(double score) {
        if(this.infinity) {
            if("-".equals(this.value)) {
                return true;
            }
            
            return false;
        }
        
        double value = Double.parseDouble(this.value);
        if(this.inclusive) {
            return value <= score;
        }
        
        return value < score;
    }
    
    public boolean greaterThan(String s) {
        if(this.infinity) {
            if("+".equals(this.value)) {
                return true;
            }
            
            return false;
        }
        
        int compare = this.value.compareTo(s);
        if(this.inclusive) {
            return compare >= 0;
        }
        
        return compare > 0;
    }

    public boolean greaterThan(double score) {
        if(this.infinity) {
            if("+".equals(this.value)) {
                return true;
            }
            
            return false;
        }
        
        double value = Double.parseDouble(this.value);
        if(this.inclusive) {
            return value >= score;
        }
        
        return value > score;
    }

    public boolean isInclusive() {
        return this.inclusive;
    }
    
    public String getValue() {
        return this.value;
    }
    
}