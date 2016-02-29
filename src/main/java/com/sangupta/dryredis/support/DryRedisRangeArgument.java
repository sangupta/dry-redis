package com.sangupta.dryredis.support;

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
        
        // TODO: parsing and sanity check
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
