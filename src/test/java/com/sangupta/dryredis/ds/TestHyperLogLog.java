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

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class TestHyperLogLog {
    
    private static final Random RANDOM = new Random();

    @Test
    public void testComputeCount() {
        HyperLogLog hyperLogLog = new HyperLogLog(16);
        hyperLogLog.offer(0);
        hyperLogLog.offer(1);
        hyperLogLog.offer(2);
        hyperLogLog.offer(3);
        hyperLogLog.offer(16);
        hyperLogLog.offer(17);
        hyperLogLog.offer(18);
        hyperLogLog.offer(19);
        hyperLogLog.offer(19);
        
        Assert.assertEquals(8, hyperLogLog.cardinality());
    }

    @Test
    public void testHighCardinality() {
        HyperLogLog hyperLogLog = new HyperLogLog(10);
        int size = 10000000;
        for (int i = 0; i < size; i++) {
            hyperLogLog.offer(streamElement(i));
        }

        long estimate = hyperLogLog.cardinality();
        double err = ((double) Math.abs(estimate - size)) / (double) size;

        Assert.assertTrue(err < 0.1d);
    }

    @Test
    public void testHighCardinality_withDefinedRSD() {
        HyperLogLog hyperLogLog = new HyperLogLog(0.01);
        int size = 100000000;
        for (int i = 0; i < size; i++) {
            hyperLogLog.offer(streamElement(i));
        }

        long estimate = hyperLogLog.cardinality();
        double err = Math.abs(estimate - size) / (double) size;
        
        Assert.assertTrue(err < .1);
    }

    @Test
    public void testMerge() {
        int numToMerge = 5;
        int bits = 16;
        int cardinality = 1000000;

        HyperLogLog[] hyperLogLogs = new HyperLogLog[numToMerge];
        HyperLogLog baseline = new HyperLogLog(bits);
        for (int i = 0; i < numToMerge; i++) {
            hyperLogLogs[i] = new HyperLogLog(bits);
            for (int j = 0; j < cardinality; j++) {
                double val = Math.random();
                hyperLogLogs[i].offer(val);
                baseline.offer(val);
            }
        }


        long expectedCardinality = numToMerge * cardinality;
        HyperLogLog hll = hyperLogLogs[0];
        hyperLogLogs = Arrays.asList(hyperLogLogs).subList(1, hyperLogLogs.length).toArray(new HyperLogLog[0]);

        long mergedEstimate = hll.merge(hyperLogLogs).cardinality();
        long baselineEstimate = baseline.cardinality();
        double se = expectedCardinality * (1.04 / Math.sqrt(Math.pow(2, bits)));

        Assert.assertTrue(mergedEstimate >= expectedCardinality - (3 * se));
        Assert.assertTrue(mergedEstimate <= expectedCardinality + (3 * se));
        Assert.assertEquals(mergedEstimate, baselineEstimate);
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    @Test
    public void testMergeWithRegisterSet() {
        HyperLogLog first = new HyperLogLog(16, new RegisterSet(1 << 20));
        HyperLogLog second = new HyperLogLog(16, new RegisterSet(1 << 20));
        first.offer(0);
        second.offer(1);
        first.merge(second);
    }

    protected static String streamElement(int i) {
        return Long.toHexString(RANDOM.nextLong());
    }
    
}
