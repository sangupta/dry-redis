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

package com.sangupta.dryredis.ds;

import com.sangupta.murmur.Murmur3;

/**
 * Class modeled around the code from
 * https://raw.githubusercontent.com/addthis/stream-lib/master/src/main/java/com/clearspring/analytics/stream/cardinality/HyperLogLog.java
 * 
 * @author sangupta
 *
 */
public class HyperLogLog implements Cloneable {
	
	private static final long SEED = 3920214;

	private final RegisterSet registerSet;
	
    private final int log2m;
    
    private final double alphaMM;
    
    @Override
    public HyperLogLog clone() {
    	HyperLogLog hll = new HyperLogLog(this.log2m);
    	
    	// copy all registers
    	hll.registerSet.setFrom(this.registerSet);
    	
    	// return instance
    	return hll;
    }


    /**
     * Create a new HyperLogLog instance using the specified standard deviation.
     *
     * @param rsd - the relative standard deviation for the counter.
     *            smaller values create counters that require more space.
     */
    public HyperLogLog(double rsd) {
        this(log2m(rsd));
    }

    /**
     * Create a new HyperLogLog instance.  The log2m parameter defines the accuracy of
     * the counter.  The larger the log2m the better the accuracy.
     * <p/>
     * accuracy = 1.04/sqrt(2^log2m)
     *
     * @param log2m - the number of bits to use as the basis for the HLL instance
     */
    public HyperLogLog(int log2m) {
        this(log2m, new RegisterSet(1 << log2m));
    }

    /**
     * Creates a new HyperLogLog instance using the given registers.  Used for unmarshalling a serialized
     * instance and for merging multiple counters together.
     *
     * @param registerSet - the initial values for the register set
     */
    HyperLogLog(int log2m, RegisterSet registerSet) {
        validateLog2m(log2m);
        this.registerSet = registerSet;
        this.log2m = log2m;
        int m = 1 << this.log2m;

        this.alphaMM = getAlphaMM(log2m, m);
    }

    private static int log2m(double rsd) {
        return (int) (Math.log((1.106 / rsd) * (1.106 / rsd)) / Math.log(2));
    }

//    private static double rsd(int log2m) {
//        return 1.106 / Math.sqrt(Math.exp(log2m * Math.log(2)));
//    }

    private static void validateLog2m(int log2m) {
        if (log2m < 0 || log2m > 30) {
            throw new IllegalArgumentException("log2m argument is " + log2m + " and is outside the range [0, 30]");
        }
    }

    private boolean offerHashed(long hashedValue) {
        // j becomes the binary address determined by the first b log2m of x
        // j will be between 0 and 2^log2m
        final int j = (int) (hashedValue >>> (Long.SIZE - log2m));
        final int r = Long.numberOfLeadingZeros((hashedValue << this.log2m) | (1 << (this.log2m - 1)) + 1) + 1;
        return registerSet.updateIfGreater(j, r);
    }

//    private boolean offerHashed(int hashedValue) {
//        // j becomes the binary address determined by the first b log2m of x
//        // j will be between 0 and 2^log2m
//        final int j = hashedValue >>> (Integer.SIZE - log2m);
//        final int r = Integer.numberOfLeadingZeros((hashedValue << this.log2m) | (1 << (this.log2m - 1)) + 1) + 1;
//        return registerSet.updateIfGreater(j, r);
//    }

    public boolean offer(Object o) {
        return offerHashed(hash(o));
    }
    
    private static long hash(Object o) {
        if (o == null) {
            return 0;
        }
        
        if (o instanceof Long) {
            return hashLong((Long) o);
        }
        
        if (o instanceof Integer) {
            return hashLong((Integer) o);
        }
        
        if (o instanceof Double) {
            return hashLong(Double.doubleToRawLongBits((Double) o));
        }
        
        if (o instanceof Float) {
            return hashLong(Float.floatToRawIntBits((Float) o));
        }
        
        if (o instanceof String) {
            return hash(((String) o).getBytes());
        }
        
        if (o instanceof byte[]) {
            byte[] bytes = (byte[]) o;
            return Murmur3.hash_x86_32(bytes, bytes.length, SEED);
        }
        
        String str = o.toString();
        if(str == null || str.isEmpty()) {
        	return 0;
        }
        
        byte[] bytes = str.getBytes();
        return Murmur3.hash_x86_32(bytes, bytes.length, SEED);
    }
    
    private static int hashLong(long data) {
        int m = 0x5bd1e995;
        int r = 24;

        int h = 0;

        int k = (int) data * m;
        k ^= k >>> r;
        h ^= k * m;

        k = (int) (data >> 32) * m;
        k ^= k >>> r;
        h *= m;
        h ^= k * m;

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }
    
    public long cardinality() {
        double registerSum = 0;
        int count = registerSet.count;
        double zeros = 0.0;
        
        for (int j = 0; j < registerSet.count; j++) {
            int val = registerSet.get(j);
            registerSum += 1.0 / (1 << val);
        
            if (val == 0) {
                zeros++;
            }
        }

        double estimate = alphaMM * (1 / registerSum);

        if (estimate <= (5.0 / 2.0) * count) {
            // Small Range Estimate
            return Math.round(linearCounting(count, zeros));
        }
        
        return Math.round(estimate);
    }

    public int sizeof() {
        return registerSet.size * 4;
    }

    /**
     * Add all the elements of the other set to this set.
     * <p/>
     * This operation does not imply a loss of precision.
     *
     * @param other A compatible Hyperloglog instance (same log2m)
     * @throws CardinalityMergeException if other is not compatible
     */
    public void addAll(HyperLogLog other) {
        if (this.sizeof() != other.sizeof()) {
            throw new IllegalArgumentException("Cannot merge estimators of different sizes");
        }

        registerSet.merge(other.registerSet);
    }

    public HyperLogLog merge(HyperLogLog... estimators) {
        HyperLogLog merged = new HyperLogLog(log2m, new RegisterSet(this.registerSet.count));
        merged.addAll(this);

        if (estimators == null) {
            return merged;
        }

        for (HyperLogLog estimator : estimators) {
            HyperLogLog hll = (HyperLogLog) estimator;
            merged.addAll(hll);
        }

        return merged;
    }

    protected static double getAlphaMM(final int p, final int m) {
        // See the paper.
        switch (p) {
            case 4:
                return 0.673 * m * m;
                
            case 5:
                return 0.697 * m * m;
                
            case 6:
                return 0.709 * m * m;
                
            default:
                return (0.7213 / (1 + 1.079 / m)) * m * m;
        }
    }

    protected static double linearCounting(int m, double V) {
        return m * Math.log(m / V);
    }
}