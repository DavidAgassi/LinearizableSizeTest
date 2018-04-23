package sizable;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by Liat n on 17/02/2018.
 */
public class SizableMap<K, V, M extends Map<K,V>, C extends LongAdder> extends Sizable<C> {
    private M map;
    protected SizableMap(M map, Class<C> counterClass){
        super(counterClass);
        try {
            this.map = map;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public V put(K key, V value){
        maxSize.increment();
        V ret = map.put(key, value);
        if(ret == null){
            minSize.increment();
        }else {
            maxSize.decrement();
        }
        return ret;
    }
    public V remove(K key){
        minSize.decrement();
        V ret = map.remove(key);
        if(ret!=null){
            maxSize.decrement();
        }else {
            minSize.increment();
        }
        return ret;
    }
    public V get(K element){
        return map.get(element);
    }

    public boolean containsKey(K key){
        return this.get(key)!= null;
    }
}
