package sizable;

import java.util.concurrent.atomic.LongAdder;

/**
 * Created by Liat n on 17/02/2018.
 */
public abstract class Sizable<C extends LongAdder> {
    protected C minSize;
    protected C maxSize;
    protected Sizable(Class<C> counterClass){
        try {
            this.minSize = counterClass.getConstructor().newInstance();
            this.maxSize = counterClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public long getSize(){
        long min;
        long max = maxSize.sum();
        while (true){
            min = minSize.sum();
            if(min>=max){
                return min;
            }
            max = maxSize.sum();
            if(min>=max){
                return max;
            }
        }
    }

    public int size(){
        return (int) getSize();
    }
}
