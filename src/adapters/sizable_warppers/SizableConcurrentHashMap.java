package adapters.sizable_warppers;

import sizable.LinearizableCounter;
import sizable.SizableMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Liat n on 17/02/2018.
 */
public class SizableConcurrentHashMap<K,V> extends SizableMap<K, V, ConcurrentHashMap<K,V>, LinearizableCounter> {
    public SizableConcurrentHashMap(){
        super(new ConcurrentHashMap<>(), LinearizableCounter.class);
    }
}
