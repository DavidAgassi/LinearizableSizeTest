package adapters.semi_sizable_wrappers;

import algorithms.published.LockFreeRelaxedAVLMap;
import sizable.SizableMap;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by Liat n on 17/02/2018.
 */
public class SemiSizableSkipList<K,V> extends SizableMap<K, V, ConcurrentSkipListMap<K,V>, LongAdder> {
    public SemiSizableSkipList(){
        super(new ConcurrentSkipListMap<>(), LongAdder.class);
    }
}
