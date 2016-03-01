package com.sangupta.dryredis.ds;

/**
 * An element that has a given priority in the sorted set. The {@link Comparable#compareTo(Object)}
 * method should NOT take this priority into account. That is taken care of by the {@link SortedSetWithPriority}
 * during insertion into its own internal data structures.
 * 
 * This helps in getting an element's priority when they are inserted using other methods.
 * 
 * @author sangupta
 *
 * @param <E>
 */
public class ElementWithPriority<E> implements Comparable<E> {
    
    private E data;
    
    private double priority;
    
    public ElementWithPriority(E data, double priority) {
        this.data = data;
        this.priority = priority;
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
    public int compareTo(E o) {
        return this.compareTo(o);
    }
    
    @Override
    public String toString() {
        return this.data.toString();
    }

}
