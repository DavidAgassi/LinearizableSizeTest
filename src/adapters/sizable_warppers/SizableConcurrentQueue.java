package adapters.sizable_warppers;

import sizable.SizableCollection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by Liat n on 17/02/2018.
 */
public class SizableConcurrentQueue<E> extends SizableCollection<E, ConcurrentLinkedQueue<E>, LongAdder> {
    public SizableConcurrentQueue(){
        super(new ConcurrentLinkedQueue<E>(), LongAdder.class);
    }
}
