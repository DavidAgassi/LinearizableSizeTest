package sizable;

import java.util.Collection;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by Liat n on 17/02/2018.
 */
public class SizableCollection<E, A extends Collection<E>, C extends LongAdder> extends Sizable {
    private A collection;
    public SizableCollection(A adt, Class<C> counterClass){
        super(counterClass);
        this.collection = adt;
    }
    public boolean add(E element){
        maxSize.increment();
        if(collection.add(element)){
            minSize.increment();
            return true;
        }else {
            maxSize.decrement();
            return false;
        }
    }
    public boolean remove(E element){
        minSize.decrement();
        if(collection.remove(element)){
            maxSize.decrement();
            return true;
        }else {
            minSize.increment();
            return false;
        }
    }
    public boolean contains(E element){
        return collection.contains(element);
    }
}
