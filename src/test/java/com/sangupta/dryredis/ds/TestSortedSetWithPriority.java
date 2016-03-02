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
