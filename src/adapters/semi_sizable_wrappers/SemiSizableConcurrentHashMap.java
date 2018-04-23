package adapters.semi_sizable_wrappers;

import sizable.SizableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by Liat n on 17/02/2018.
 */
public class SemiSizableConcurrentHashMap<K,V> extends SizableMap<K, V, ConcurrentHashMap<K,V>, LongAdder> {
    public SemiSizableConcurrentHashMap(){
        super(new ConcurrentHashMap<>(), LongAdder.class);
    }
}
