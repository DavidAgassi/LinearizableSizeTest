package adapters.semi_sizable_wrappers;

import algorithms.published.LockFreeRelaxedAVLMap;
import sizable.SizableMap;

import java.util.concurrent.atomic.LongAdder;

/**
 * Created by Liat n on 17/02/2018.
 */
public class SemiSizableLockFreeRelaxedAVLMap<K,V> extends SizableMap<K, V, LockFreeRelaxedAVLMap<K,V>, LongAdder> {
    public SemiSizableLockFreeRelaxedAVLMap(){
        super(new LockFreeRelaxedAVLMap<>(), LongAdder.class);
    }
}
