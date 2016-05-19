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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sangupta.dryredis.support.DryRedisSetAggregationType;

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
    
    @Override
    public SortedSetWithPriority<E> clone() {
        SortedSetWithPriority<E> newSet = new SortedSetWithPriority<E>();
        
        for(ElementWithPriority<E> element : this.delegate) {
            newSet.add(element.clone());
        }
        
        return newSet;
    }
    
    /**
     * Apply a new weight to priority of all elements in this set.
     * 
     * @param weight
     */
    public void applyWeight(double weight) {
        if(weight == 1.0d) {
            // nothing to do
            return;
        }
        
        // modify the weigths in original objects
        Iterator<ElementWithPriority<E>> iterator = this.iterator();
        while(iterator.hasNext()) {
            ElementWithPriority<E> element = iterator.next();
            element.setPriority(element.getPriority() * weight);

            // for priority list
            this.priorities.put(element, element.getPriority());
        }
    }

    public Iterator<ElementWithPriority<E>> descendingIterator() {
        return this.delegate.descendingIterator();
    }
    
    public Double getPriority(ElementWithPriority<E> element) {
        return this.priorities.get(element);
    }
    
    public Double getPriority(E element) {
        return this.priorities.get(new ElementWithPriority<E>(element, 0));
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
    
    public boolean addAll(SortedSetWithPriority<E> set, double weight, DryRedisSetAggregationType aggregation) {
        boolean modified = false;
        
        for(ElementWithPriority<E> incomingElement : set.delegate) {
            Double existingPriority = this.priorities.get(incomingElement);
            if(existingPriority != null) {
                // update the existing priority
                
                double incomingPriority = set.getPriority(incomingElement);
                incomingPriority = incomingPriority * weight;
                
                // choose final priority
                double finalPriority;
                
                switch(aggregation) {
                    case MAX:
                        finalPriority = Math.max(existingPriority, incomingPriority);
                        break;
                    
                    case MIN:
                        finalPriority = Math.min(existingPriority, incomingPriority);
                        break;

                    case SUM:
                        finalPriority = existingPriority + incomingPriority;
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown aggregation type");
                }
                
                if(finalPriority != incomingPriority) {
                    // priority needs to be updated
                    // resorting will happen
                    this.remove(incomingElement);
                    
                    // update priority of element
                    this.add(new ElementWithPriority<E>(incomingElement.getData(), finalPriority));
                    
                    // updated
                    modified = true;
                }
            } else {
                // new element
                this.add(incomingElement);
                modified = true;
            }
        }
        
        return modified;
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

    public boolean retainAll(SortedSetWithPriority<E> set, double weight, DryRedisSetAggregationType aggregation) {
        boolean modified = false;
        
        Iterator<ElementWithPriority<E>> iterator = this.iterator();
        List<ElementWithPriority<E>> toBeAdded = new ArrayList<ElementWithPriority<E>>();
        
        while(iterator.hasNext()) {
            ElementWithPriority<E> ourElement = iterator.next();
            
            Double incomingPriority = set.getPriority(ourElement);
            if(incomingPriority == null) {
                // the element is not present in the incoming set
                // remove from current set
                this.remove(ourElement);
                modified = true;
                continue;
            }
            
            // update the existing priority
            
            double existingPriority = this.priorities.get(ourElement);
            incomingPriority = incomingPriority * weight;
            
            // choose final priority
            double finalPriority;
            
            switch(aggregation) {
                case MAX:
                    finalPriority = Math.max(existingPriority, incomingPriority);
                    break;
                
                case MIN:
                    finalPriority = Math.min(existingPriority, incomingPriority);
                    break;

                case SUM:
                    finalPriority = existingPriority + incomingPriority;
                    break;

                default:
                    throw new IllegalArgumentException("Unknown aggregation type");
            }
            
            if(finalPriority != existingPriority) {
                // priority needs to be updated
                // resorting will happen
                iterator.remove();
                
                // update priority of element
                ourElement.setPriority(finalPriority);
                
                // these are added - to prevent concurrentmodificationexception
                toBeAdded.add(ourElement);
                
                // updated
                modified = true;
            }
        }
        
        if(!toBeAdded.isEmpty()) {
            for(ElementWithPriority<E> element : toBeAdded) {
                this.add(element);
            }
        }
        
        return modified;
    }
    
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        
        for(ElementWithPriority<E> ourElement : this.delegate) {
            if(!c.contains(ourElement)) {
                this.remove(ourElement);
                modified = true;
            }
        }
        
        return modified;
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