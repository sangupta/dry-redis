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

/**
 * An element that has a given priority in the sorted set. The
 * {@link Comparable#compareTo(Object)} method should NOT take this priority
 * into account. That is taken care of by the {@link SortedSetWithPriority}
 * during insertion into its own internal data structures.
 * 
 * This helps in getting an element's priority when they are inserted using
 * other methods.
 * 
 * @author sangupta
 *
 * @param <E>
 *            the type of element being stored
 */
public class ElementWithPriority<E extends Comparable<E>> implements Comparable<E>, Cloneable {
    
    /**
     * The data that we store
     */
    private E data;
    
    /**
     * Associated priority of the element
     */
    private double priority;
    
    /**
     * Convenience constructor.
     * 
     * @param data
     *            the data to be contained in this element
     * 
     * @param priority
     *            the priority of this element
     */
    public ElementWithPriority(E data, double priority) {
        this.data = data;
        this.priority = priority;
    }
    
    @Override
    public ElementWithPriority<E> clone() {
        return new ElementWithPriority<E>(this.data, this.priority);
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }
    
    @Override
    public int hashCode() {
        return this.data.hashCode();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        
        if(this == obj) {
            return true;
        }
        
        if(obj instanceof ElementWithPriority) {
            return this.data.equals(((ElementWithPriority<E>) obj).data);
        }
        
        return this.data.equals(obj);
    }

    @Override
    public int compareTo(E other) {
        if(other == null) {
            return -1;
        }
        
        if(this.data == other) {
            return 0;
        }
        
        return this.data.compareTo(other);
    }
    
    @Override
    public String toString() {
        return this.data.toString();
    }

}
