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

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link SortedSetWithPriority} class.
 *  
 * @author sangupta
 *
 */
public class TestSortedSetWithPriority {
    
    private static final int MAX_ELEMENTS = 1000 * 100; // 100k elements

    @Test
    public void testSet() {
        SortedSetWithPriority<String> set = new SortedSetWithPriority<String>();
        
        final int maxPriority = 2 * MAX_ELEMENTS;
        for(int index = 0; index < MAX_ELEMENTS; index++) {
            set.add(new ElementWithPriority<String>(String.valueOf(index), maxPriority - index));
        }

        // check size
        Assert.assertEquals(MAX_ELEMENTS, set.size());
        
        // check iteration
        Iterator<ElementWithPriority<String>> iterator = set.iterator();
        int count = 0;
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            Assert.assertEquals(String.valueOf(MAX_ELEMENTS - count - 1), element.getData());
            Assert.assertEquals(MAX_ELEMENTS + count + 1, element.getPriority(), 0d);
            count++;
        }
        
        Assert.assertEquals(MAX_ELEMENTS, count);
        
        // check descending iterator
        iterator = set.descendingIterator();
        count = 0;
        while(iterator.hasNext()) {
            ElementWithPriority<String> element = iterator.next();
            Assert.assertEquals(String.valueOf(count), element.getData());
            Assert.assertEquals(maxPriority - count, element.getPriority(), 0d);
            count++;
        }
        
        Assert.assertEquals(MAX_ELEMENTS, count);
    }
}
