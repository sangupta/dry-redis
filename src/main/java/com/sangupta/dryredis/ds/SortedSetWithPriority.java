package com.sangupta.dryredis.ds;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A {@link SortedSet} implementation that uses a floating-point priority for
 * sorting first, and then natural sorting if needed.
 * 
 * @author sangupta
 *
 * @param <E>
 */
public class SortedSetWithPriority<E extends Comparable<E>> implements SortedSet<ElementWithPriority<E>>, Cloneable {

    private final TreeSet<ElementWithPriority<E>> delegate;

    private final Map<ElementWithPriority<E>, Double> priorities = new HashMap<ElementWithPriority<E>, Double>();

    public SortedSetWithPriority() {
        this.delegate = new TreeSet<ElementWithPriority<E>>(new Comparator<ElementWithPriority<E>>() {

            @Override
            public int compare(ElementWithPriority<E> object1, ElementWithPriority<E> object2) {
                if(object1 == object2) {
                    return 0;
                }
                
                Double priority1 = SortedSetWithPriority.this.priorities.get(object1);
                if(priority1 == null) {
                    priority1 = object1.getPriority();
                }
                
                Double priority2 = SortedSetWithPriority.this.priorities.get(object2);
                if(priority2 == null) {
                    priority2 = object2.getPriority();
                }

                int compare = priority1.compareTo(priority2);
                if (compare != 0) {
                    return compare;
                }

                return object1.getData().compareTo(object2.getData());
            }

        });
    }
    
    public Iterator<ElementWithPriority<E>> descendingIterator() {
        return this.delegate.descendingIterator();
    }
    
    public Double getPriority(E element) {
        return this.priorities.get(element);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override
    public Iterator<ElementWithPriority<E>> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.delegate.toArray(a);
    }

    @Override
    public boolean add(ElementWithPriority<E> e) {
        // ordering is important here
        boolean added = this.delegate.add(e);
        if(added) {
            this.priorities.put(e, e.getPriority());
        }
        
        return added;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = this.delegate.remove(o);
        if(removed) {
            this.priorities.remove(o);
        }
        
        return removed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ElementWithPriority<E>> c) {
        boolean added = false;
        for(ElementWithPriority<E> element : c) {
            if(this.add(element)) {
                added = true;
            }
        }
        
        return added;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removed = false;
        for(Object o : c) {
            if(this.remove(o)) {
                removed = true;
            }
        }
        
        return removed;
    }

    @Override
    public void clear() {
        this.delegate.clear();
        this.priorities.clear();
    }

    @Override
    public Comparator<? super ElementWithPriority<E>> comparator() {
        return this.delegate.comparator();
    }

    @Override
    public SortedSet<ElementWithPriority<E>> subSet(ElementWithPriority<E> fromElement, ElementWithPriority<E> toElement) {
        return this.delegate.subSet(fromElement, toElement);
    }

    @Override
    public SortedSet<ElementWithPriority<E>> headSet(ElementWithPriority<E> toElement) {
        return this.delegate.headSet(toElement);
    }

    @Override
    public SortedSet<ElementWithPriority<E>> tailSet(ElementWithPriority<E> fromElement) {
        return this.delegate.tailSet(fromElement);
    }

    @Override
    public ElementWithPriority<E> first() {
        return this.delegate.first();
    }

    @Override
    public ElementWithPriority<E> last() {
        return this.delegate.last();
    }
    
}
