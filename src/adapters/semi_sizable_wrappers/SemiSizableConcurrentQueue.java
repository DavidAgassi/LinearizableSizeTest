package adapters.semi_sizable_wrappers;

import sizable.SizableCollection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by Liat n on 17/02/2018.
 */

public class SemiSizableConcurrentQueue<E> extends SizableCollection<E, ConcurrentLinkedQueue<E>, LongAdder> {
public SemiSizableConcurrentQueue(){
        super(new ConcurrentLinkedQueue<>(), LongAdder.class);
        }
}
