package adapters.sizable_warppers;

import algorithms.published.LockFreeRelaxedAVLMap;
import sizable.LinearizableCounter;
import sizable.SizableMap;

import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by Liat n on 17/02/2018.
 */
public class SizableSkipList<K,V> extends SizableMap<K, V, ConcurrentSkipListMap<K,V>, LinearizableCounter> {
    public SizableSkipList(){
        super(new ConcurrentSkipListMap<>(), LinearizableCounter.class);
    }
}
