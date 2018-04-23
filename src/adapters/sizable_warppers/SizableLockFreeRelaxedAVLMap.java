package adapters.sizable_warppers;

import algorithms.published.LockFreeRelaxedAVLMap;
import sizable.LinearizableCounter;
import sizable.SizableMap;

/**
 * Created by Liat n on 17/02/2018.
 */
public class SizableLockFreeRelaxedAVLMap<K,V> extends SizableMap<K, V, LockFreeRelaxedAVLMap<K,V>, LinearizableCounter> {
    public SizableLockFreeRelaxedAVLMap(){
        super(new LockFreeRelaxedAVLMap<>(), LinearizableCounter.class);
    }
}
