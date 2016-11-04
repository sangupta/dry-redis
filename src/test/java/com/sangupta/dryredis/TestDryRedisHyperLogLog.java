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

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link DryRedisHyperLogLog}.
 * 
 * @author sangupta
 *
 */
public class TestDryRedisHyperLogLog {

    @Test
    public void test() {
    	DryRedisHyperLogLogOperations hll = getRedis();
    			
        Assert.assertEquals(1, hll.pfadd("test"));
        Assert.assertEquals(0, hll.pfadd("test"));
        
        Assert.assertEquals(0, hll.pfcount("test"));
        Random random = new Random();
        int size = 1000 * 1000;
        for(int index = 0; index < size; index++) {
        	hll.pfadd("test", String.valueOf(random.nextInt()));
        }
        
        double actual = hll.pfcount("test");
        double error = (actual - size) / size;
        Assert.assertTrue(error < 0.1d);
    }
    
//    @Test
//    public void testUnionCount() {
//    	DryRedisHyperLogLogOperations hll = getRedis();
//		
//        hll.pfadd("test", "foo");
//        hll.pfadd("test", "bar");
//        hll.pfadd("test", "zap");
//        
//        hll.pfadd("test2", "1");
//        hll.pfadd("test2", "2");
//        hll.pfadd("test2", "3");
//        
//        Assert.assertEquals(6, hll.pfcount(Arrays.asList("test", "test2")));
//    }
    
    protected DryRedisHyperLogLogOperations getRedis() {
        return new DryRedisHyperLogLog();
    }
    
}
