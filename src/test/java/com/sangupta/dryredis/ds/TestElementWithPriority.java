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

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ElementWithPriority} class.
 * 
 * @author sangupta
 *
 */
public class TestElementWithPriority {

    @Test
    public void testSameDataDiffPriority() {
        ElementWithPriority<String> ewp1 = new ElementWithPriority<String>("hello", 1d);
        ElementWithPriority<String> ewp2 = new ElementWithPriority<String>("hello", 2d);
        
        Assert.assertNotNull(ewp1.getData());
        Assert.assertNotNull(ewp2.getData());
        Assert.assertEquals("hello", ewp1.getData());
        Assert.assertEquals("hello", ewp2.getData());
        
        Assert.assertNotEquals(ewp1.getPriority(), ewp2.getPriority(), 0d);
        
        Assert.assertEquals("hello".hashCode(), ewp1.hashCode());
        Assert.assertEquals(ewp1.hashCode(), ewp2.hashCode());
        Assert.assertEquals(ewp1, ewp2);
        
        Assert.assertFalse(ewp1.equals(null));
        Assert.assertFalse(ewp1.equals(""));
        Assert.assertTrue(ewp1.equals("hello"));
        Assert.assertTrue(ewp1.equals(ewp2));
        Assert.assertTrue(ewp1.equals(ewp1));
        
        Assert.assertEquals("hello", ewp1.toString());
    }
    
    @Test
    public void testDiffDataSamePriority() {
        ElementWithPriority<String> ewp1 = new ElementWithPriority<String>("hello1", 1d);
        ElementWithPriority<String> ewp2 = new ElementWithPriority<String>("hello2", 1d);
        
        Assert.assertNotNull(ewp1.getData());
        Assert.assertNotNull(ewp2.getData());
        Assert.assertEquals("hello1", ewp1.getData());
        Assert.assertEquals("hello2", ewp2.getData());
        
        Assert.assertEquals(ewp1.getPriority(), ewp2.getPriority(), 0d);
        
        Assert.assertEquals("hello1".hashCode(), ewp1.hashCode());
        Assert.assertEquals("hello2".hashCode(), ewp2.hashCode());
        Assert.assertNotEquals(ewp1.hashCode(), ewp2.hashCode());
        Assert.assertNotEquals(ewp1, ewp2);
    }
    
    @Test
    public void testClone() {
        ElementWithPriority<String> ewp1 = new ElementWithPriority<String>("hello", 1d);
        ElementWithPriority<String> ewp2 = ewp1.clone();
        
        Assert.assertNotNull(ewp1.getData());
        Assert.assertNotNull(ewp2.getData());
        Assert.assertEquals("hello", ewp1.getData());
        Assert.assertEquals("hello", ewp2.getData());
        
        Assert.assertEquals(ewp1.getPriority(), ewp2.getPriority(), 0d);
        
        Assert.assertEquals("hello".hashCode(), ewp1.hashCode());
        Assert.assertEquals(ewp1.hashCode(), ewp2.hashCode());
        Assert.assertEquals(ewp1, ewp2);
    }
}