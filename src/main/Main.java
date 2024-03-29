/**
 * Java test harness for throughput experiments on concurrent data structures.
 * Copyright (C) 2012 Trevor Brown
 * Contact (me [at] tbrown [dot] pro) with any questions or comments.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package main;

import org.deuce.transform.Exclude;

import adapters.*;
import main.support.*;
import java.io.*;
import java.lang.management.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Exclude
public class Main {

    // central list of factory classes for all supported data structures
    
    static final ArrayList<TreeFactory<Integer>> factories =
            new ArrayList<TreeFactory<Integer>>();
    static {
//        factories.add(new LockFreeChromaticFactory<Integer>());
//        factories.add(new LockFreeBSlackTreeFactory<Integer>());
//        factories.add(new LockFree4STFactory<Integer>());
//        factories.add(new LockFreeKSTRQFactory<Integer>());
//        factories.add(new SkipListDeuceSTMFactory<Integer>());
//        factories.add(new RBTreeDeuceSTMFactory<Integer>());
//        factories.add(new RBTreeCoarseLockFactory<Integer>());
//        factories.add(new RBTreeUnsyncFactory<Integer>());
//        factories.add(new DummyFactory<Integer>());
//        factories.add(new OptTreeFactory<Integer>());
//        factories.add(new SnapTreeFactory<Integer>());
//        factories.add(new SkipTreeFactory<Integer>());
//        factories.add(new Ctrie2Factory<Integer>());
//        factories.add(new StaticDictionary5Factory<Integer>());
//        factories.add(new HashMapFactory<Integer>());
//        factories.add(new SyncTreeMapFactory<Integer>());
//        factories.add(new TreeMapFactory<Integer>());
        factories.add(new SkipListFactory<Integer>());
        factories.add(new SemiSizableSkipListFactory<Integer>());
        factories.add(new SizableSkipListFactory<Integer>());

        factories.add(new LockFreeAVLFactory<Integer>());
        factories.add(new SizableLockFreeAVLFactory<Integer>());
        factories.add(new SemiSizableLockFreeAVLFactory<Integer>());

        factories.add(new ConcurrentHashMapFactory<Integer>());
        factories.add(new SizableConcurrentHashMapFactory<Integer>());
        factories.add(new SemiSizableConcurrentHashMapFactory<Integer>());

    }

    // factory classes for each supported data structure

    protected static abstract class TreeFactory<K> {
        public abstract SetInterface<K> newTree(final Object param);
        public abstract String getName();
    }

    @Exclude
    protected static class LockFreeChromaticFactory<K> extends TreeFactory<K> {
        Object param;
        public SetInterface<K> newTree(final Object param) {
            this.param = param;
            if ("".equals(param.toString()) || param == null) {
                return new LockFreeChromaticAdapter();
            }
            return new LockFreeChromaticAdapter(Integer.parseInt(param.toString()));
        }
        public String getName() { return "Chromatic"; }
    }
    
    @Exclude
    protected static class LockFreeBSlackTreeFactory<K> extends TreeFactory<K> {
        Object param;
        public SetInterface<K> newTree(final Object param) {
            this.param = param;
            if ("".equals(param.toString()) || param == null) {
                return new LockFreeBSlackTreeAdapter();
            }
            return new LockFreeBSlackTreeAdapter(Integer.parseInt(param.toString()));
        }
        public String getName() { return "ConcurrentBSlack"; }
    }

    @Exclude
    protected static class SkipListDeuceSTMFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new SkipListDeuceSTMAdapter(1.0);
        }
        public String getName() { return "SkipListSTM"; }
    }

    @Exclude
    protected static class RBTreeDeuceSTMFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new RBTreeDeuceSTMAdapter(1.0);
        }
        public String getName() { return "RBSTM"; }
    }

    @Exclude
    protected static class RBTreeCoarseLockFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new RBTreeCoarseLockAdapter(1.0);
        }
        public String getName() { return "RBLock"; }
    }

    @Exclude
    protected static class RBTreeUnsyncFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new RBTreeUnsyncAdapter(1.0);
        }
        public String getName() { return "RBUnsync"; }
    }

    @Exclude
    protected static class DummyFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new DummyAdapter(1.0);
        }
        public String getName() { return "Dummy"; }
    }

    @Exclude
    protected static class LockFreeAVLFactory<K> extends TreeFactory<K> {
        Object param;
        public SetInterface<K> newTree(final Object param) {
            this.param = param;
            if ("".equals(param.toString()) || param == null) {
                return new LockFreeAVLAdapter();
            }
            return new LockFreeAVLAdapter(Integer.parseInt(param.toString()));
        }
        public String getName() { return "LockFreeAVL"; }
    }

    @Exclude
    protected static class LockFree4STFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new LockFree4STAdapter();
        }
        public String getName() { return "4-ST"; }
    }

    @Exclude
    protected static class LockFreeKSTRQFactory<K> extends TreeFactory<K> {
        Object param;
        public SetInterface<K> newTree(final Object param) {
            this.param = param;
            return new LockFreeKSTRQAdapter(Integer.parseInt(param.toString()));
        }
        public String getName() { return "KST" /*+ (param != null ? param.toString() : "")*/; }
    }

    @Exclude
    protected static class OptTreeFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new OptTreeAdapter();
        }
        public String getName() { return "AVL"; }
    }

    @Exclude
    protected static class SnapTreeFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new SnapTreeAdapter();
        }
        public String getName() { return "Snap"; }
    }

    @Exclude
    protected static class SkipListFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new ConcurrentSkipListMapAdapter();
        }
        public String getName() { return "SkipList"; }
    }
    @Exclude
    protected static class SizableSkipListFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new SizableSkipListAdapter();
        }
        public String getName() { return "SizableSkipList"; }
    }@Exclude
    protected static class SemiSizableSkipListFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new SemiSizableSkipListAdapter();
        }
        public String getName() { return "SemiSizableSkipList"; }
    }

    @Exclude
    protected static class ConcurrentHashMapFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new ConcurrentHashMapAdapter();
        }
        public String getName() { return "ConcurrentHMAP"; }
    }

    @Exclude
    protected static class SkipTreeFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new SkipTreeAdapter();
        }
        public String getName() { return "SkipTree"; }
    }

    @Exclude
    protected static class Ctrie2Factory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new Ctrie2Adapter();
        }
        public String getName() { return "Ctrie"; }
    }

    @Exclude
    protected static class StaticDictionary5Factory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new LockFreeBSTAdapter();
        }
        public String getName() { return "BST"; }
    }

    @Exclude
    protected static class SyncTreeMapFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new SyncTreeMapAdapter();
        }
        public String getName() { return "SyncTMAP"; }
    }

    @Exclude
    protected static class TreeMapFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new TreeMapAdapter();
        }
        public String getName() { return "TMAP"; }
    }

    @Exclude
    protected static class HashMapFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) {
            return new HashMapAdapter();
        }
        public String getName() { return "HMAP"; }
    }

    @Exclude
    protected static class SizableLockFreeAVLFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) { return new SizableLockFreeAVLAdapter(); }
        public String getName() { return "SizableLFAVL"; }
    }
    @Exclude
    protected static class SemiSizableLockFreeAVLFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) { return new SemiSizableLockFreeAVLAdapter(); }
        public String getName() { return "SemiSizableLFAVL"; }
    }
    @Exclude
    protected static class SizableConcurrentHashMapFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) { return new SizableConcurrentHashMapAdapter(); }
        public String getName() { return "SizableHMAP"; }
    }
    @Exclude
    protected static class SemiSizableConcurrentHashMapFactory<K> extends TreeFactory<K> {
        public SetInterface<K> newTree(final Object param) { return new SemiSizableConcurrentHashMapAdapter(); }
        public String getName() { return "SemiSizableHMAP"; }
    }
    
    // some variables for the test harness
    protected final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    public static final int RAW_NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static final int NUMBER_OF_PROCESSORS = RAW_NUMBER_OF_PROCESSORS == 8 ? 4 : RAW_NUMBER_OF_PROCESSORS; // override for hyperthreading on i7
    public static final boolean PRINT_FREEMEM = false; // note: just a (rather inaccurate) estimate
    private long startFreemem = 0;

    // variables for the experiment
    protected String machine;
    protected int nthreads;
    protected int ntrials;
    protected double nseconds;
    protected String filename;
    protected Ratio ratio;
    protected String alg;
    protected SwitchMap switches;
    protected boolean prefill;
    protected Object treeParam;
    
    // some timing variables
    protected AtomicLong startUserTime = new AtomicLong(0);
    protected AtomicLong startWallTime = new AtomicLong(0);
    
    public Main(int nthreads, int ntrials, double nseconds, String filename,
            Ratio ratio, String alg, SwitchMap switches, boolean prefill, Object treeParam) {
        this.nthreads = nthreads;
        this.ntrials = ntrials;
        this.nseconds = nseconds;
        this.filename = filename;
        this.ratio = ratio;
        this.alg = alg;
        this.switches = switches;
        this.prefill = prefill;
        this.treeParam = treeParam;
        try {
            Scanner in = new Scanner(new File("machine"));
            this.machine = in.next();
            in.close();
        } catch (Exception ex) {
            System.err.println("machine file not found. everything is still okay.");
            this.machine = "unnamed";
        }
    }
    
    @Exclude
    protected abstract class Generator<K> {
        final Random rng;
        public Generator(final Random rng) { this.rng = rng; }
        public abstract K next();
    }

    @Exclude
    public final class RandomGenerator extends Generator<Integer> {
        final int maxKey;
        final int id, numberOfIds;

        public RandomGenerator(final int id, final int numberOfIds, final Random rng, final int maxKey) {
            super(rng);
            if (maxKey < 0) throw new RuntimeException("maxKey must be > 0");
            this.maxKey = maxKey;
            this.id = id;
            this.numberOfIds = numberOfIds;
        }

        public Integer next() {
            return rng.nextNatural(maxKey)+1;
        }
    }

    @Exclude
    public final class LeftRightChainGenerator extends Generator<Integer> {
        final static int SCALE = 1;
        final int maxKey;
        final int id;
        final int originalChainSize;
        int chainSize;
        final static int LEFT = -1, RIGHT = 1;
        int rand = -1, last = -1, dir = RIGHT, cnt = 0;

        public LeftRightChainGenerator(final int id, final int chainSize, final Random rng, final int maxKey) {
            super(rng);
            if (maxKey < 1) throw new RuntimeException("maxKey must be >= 1 so that each process is guaranteed a key.");
            this.originalChainSize = chainSize;
            this.maxKey = maxKey;
            this.id = id;
        }

        public Integer next() {
            if (cnt == 0) {
                rand = rng.nextNatural();
                chainSize = rand % originalChainSize + 1;
                //System.out.println("chainsize = " + chainSize);
                if (rand <= Integer.MAX_VALUE / 2) {
                    dir = LEFT;
                } else {
                    dir = RIGHT;
                }
            }
            cnt = (cnt + SCALE) % (chainSize*SCALE);
            return (rand + (dir == LEFT ? -cnt : cnt) + maxKey) % maxKey;
        }
    }
        
    @Exclude
    public abstract class GeneratorFactory {
        abstract ArrayList<Generator> getGenerators(Experiment ex, java.util.Random experimentRng);
        abstract String getName();
    }

    @Exclude
    public final class RandomGeneratorFactory extends GeneratorFactory {
        ArrayList<Generator> getGenerators(Experiment ex, java.util.Random rng) {
            ArrayList<Generator> arrays = new ArrayList<Generator>(ex.nprocs);
            for (int i=0;i<ex.nprocs;i++) {
                arrays.add(new RandomGenerator(i, ex.nprocs, new Random(rng.nextInt()), ex.maxkey));
            }
            return arrays;
        }
        String getName() { return "overlapRandom"; }
    }

    @Exclude
    final class LeftRightChainGeneratorFactory extends GeneratorFactory {
        private final int chainSize;
        public LeftRightChainGeneratorFactory(int chainSize) { this.chainSize = chainSize; }
        ArrayList<Generator> getGenerators(Experiment ex, java.util.Random rng) {
            ArrayList<Generator> arrays = new ArrayList<Generator>(ex.nprocs);
            for (int i=0;i<ex.nprocs;i++) {
                arrays.add(new LeftRightChainGenerator(i, chainSize, new Random(rng.nextInt()), ex.maxkey));
            }
            return arrays;
        }
        String getName() { return "overlapLeftRightChain"; }
    }

    @Exclude
    public abstract class Worker<K extends Comparable<? super K>> extends Thread {
        public abstract int getOpCount();
        public abstract int getTrueIns();
        public abstract int getFalseIns();
        public abstract int getTrueDel();
        public abstract int getFalseDel();
        public abstract int getTrueFind();
        public abstract int getFalseFind();
        public abstract int getTrueRQ();
        public abstract int getFalseRQ();
        public abstract int getTrueSnap();
        public abstract int getFalseSnap();
        public abstract long getEndTime();
        public abstract long getStartTime();
        public abstract long getMyStartCPUTime();
        public abstract long getMyStartUserTime();
        public abstract long getMyStartWallTime();
        public abstract long getUserTime();
        public abstract long getWallTime();
        public abstract long getCPUTime();
        public abstract long getKeysum();
        public abstract long getSizeOps();
        public abstract long getSizeDuration();
    }
    
    @Exclude
    public class TimedWorker<K extends Comparable<? super K>> extends Worker<K> {
        public final long WORK_TIME;
        CyclicBarrier start;
        Generator<Integer> gen;
        AbstractAdapter<K> tree;
        int trueDel, falseDel, trueIns, falseIns, trueFind, falseFind, trueRQ, falseRQ, trueSnap, falseSnap;
        long keysum; // sum of new keys inserted by this thread minus keys deleted by this thread
        final Experiment ex;
        Random rng;

        private long id;
        private ThreadMXBean bean;
        
        public final AtomicLong sharedStartUserTime;
        public final AtomicLong sharedStartWallTime;
        public long myStartCPUTime;
        public long myStartUserTime;
        public long myStartWallTime;
        public long cpuTime;
        public long userTime;
        public long wallTime;
        public long sizeTime;
        public long sizeOps;
        public ArrayList<Worker> workers3; // ref to containing array [dirty technique :P...]

        public TimedWorker(final long WORK_TIME,
                           final Generator gen,
                           final Experiment ex,
                           final java.util.Random rng,
                           final AbstractAdapter<K> tree,
                           final CyclicBarrier start,
                           final AtomicLong sharedStart,
                           final AtomicLong sharedStartWallTime,
                           final ArrayList<Worker> workers) {
            this.WORK_TIME = WORK_TIME;
            this.gen = gen;
            this.ex = ex;
            this.rng = new Random(rng.nextInt());
            this.tree = tree;
            this.start = start;
            this.sharedStartUserTime = sharedStart;
            this.workers3 = workers;
            this.sharedStartWallTime = sharedStartWallTime;
        }

        @Override
        @SuppressWarnings("empty-statement")
        public final void run() {
            bean = ManagementFactory.getThreadMXBean();
            if (!bean.isCurrentThreadCpuTimeSupported()) {
                System.out.println("THREAD CPU TIME UNSUPPORTED");
                System.exit(-1);
            }
            if (!bean.isThreadCpuTimeEnabled()) {
                System.out.println("THREAD CPU TIME DISABLED");
                System.exit(-1);
            }
            id = java.lang.Thread.currentThread().getId();
            
            // everyone waits on barrier
            if (start != null) try { start.await(); } catch (Exception e) { e.printStackTrace(); System.exit(-1); }
            
            // everyone waits until main thread sets experiment state to RUNNING
            while (ex.state == ExperimentState.PENDING);

            // start timing
            myStartUserTime = bean.getThreadUserTime(id);
            myStartCPUTime = bean.getThreadCpuTime(id);
            myStartWallTime = System.nanoTime();
            sharedStartUserTime.compareAndSet(0, myStartUserTime);
            sharedStartWallTime.compareAndSet(0, myStartWallTime);
            
            // perform operations while experiment's state is running
            while (ex.state == ExperimentState.RUNNING) {
                final K key = (K) gen.next();
                final double op = rng.nextNatural() / (double) Integer.MAX_VALUE;
                if (op < ratio.ins) {
                    if (tree.add(key, rng)) {
                        keysum += (Integer) 1;
                        trueIns++;
                    } else falseIns++;                    
                } else if (op < ratio.ins + ratio.del) {
                    if (tree.remove(key, rng)) {
                        keysum -= (Integer) 1;
                        trueDel++;
                    } else falseDel++;
                } else if (op < ratio.ins + ratio.del + ratio.size) {
                    long startTime = System.nanoTime();
                    tree.getKeysum();
                    this.sizeTime += System.nanoTime() - startTime;
                    this.sizeOps ++;
                } else {
                    if (tree.contains(key)) trueFind++;
                    else falseFind++;
                }
            }
            
            // finish timing
            wallTime = System.nanoTime();
            userTime = bean.getThreadUserTime(id);
            cpuTime = bean.getThreadCpuTime(id);
        }

        public int getOpCount() { return 0; }
        public int getTrueIns() { return trueIns; }
        public int getFalseIns() { return falseIns; }
        public int getTrueDel() { return trueDel; }
        public int getFalseDel() { return falseDel; }
        public int getTrueFind() { return trueFind; }
        public int getFalseFind() { return falseFind; }
        public int getTrueRQ() { return trueRQ; }
        public int getFalseRQ() { return falseRQ; }
        public int getTrueSnap() { return trueSnap; }
        public int getFalseSnap() { return falseSnap; }
        public long getStartTime() { return myStartWallTime; }
        public long getEndTime() { return wallTime; }
        public long getMyStartCPUTime() { return myStartCPUTime; }
        public long getMyStartUserTime() { return myStartUserTime; }
        public long getMyStartWallTime() { return myStartWallTime; }
        public long getUserTime() { return userTime; }
        public long getWallTime() { return wallTime; }
        public long getCPUTime() { return wallTime; }
        public long getKeysum() { return keysum; }

        @Override
        public long getSizeOps() {
            return sizeOps;
        }

        @Override
        public long getSizeDuration() {
            if(sizeOps==0) return 0;
            return sizeTime/sizeOps;
        }
    }
    
    @Exclude
    final class BoolHolder { volatile boolean b; }
    
    @Exclude
    final class FixedNumberOfOpsWorker<K extends Comparable<? super K>> extends Thread {
        final AbstractAdapter<K> tree;
        final CyclicBarrier start, end;
        final int opsToPerform;
        final Random rng;
        final Ratio ratio;
        final int maxkey;
        final BoolHolder done;
        long keysum;
        
        public FixedNumberOfOpsWorker(
                final AbstractAdapter<K> tree,
                final int opsToPerform,
                final Ratio ratio,
                final int maxkey,
                final Random rng,
                final CyclicBarrier start,
                final CyclicBarrier end,
                final BoolHolder done) {
            this.tree = tree;
            this.opsToPerform = opsToPerform;
            this.ratio = ratio;
            this.maxkey = maxkey;
            this.rng = rng;
            this.start = start;
            this.end = end;
            this.done = done;
        }
        
        @Override
        public void run() {
            try { start.await(); } catch (Exception ex) { ex.printStackTrace(); System.exit(-1); }
            Integer ra;
            for (int i=0; i < opsToPerform && !done.b; i++) {
                int key = rng.nextNatural(maxkey)+1;
                ra = rng.nextNatural();
                if (ra < ratio.ins * Integer.MAX_VALUE) {
                    if (tree.add((K) (Integer) key, rng)) keysum += 1;
                } else if(ra < (ratio.ins + ratio.del)* Integer.MAX_VALUE ) {
                    if (tree.remove((K) (Integer) key, rng)) keysum -= 1;
                }
            }

            done.b = true;
//            System.out.println("done");
//            System.out.println("worker waiting" + end.getNumberWaiting());
            try { end.await(); } catch (Exception ex) { ex.printStackTrace(); System.exit(-1); }
        }
        
        public long getKeysum() {
            return keysum;
        }
    }
    
    protected boolean runTrial(
            final PrintStream out,
            final boolean discardResults,
            final boolean shouldMeasureTrees,
            final String prefix,
            final SizeKeysumPair pair,
            final java.util.Random rng,
            final AbstractAdapter<Integer> tree,
            final Experiment ex) {
        
        if (ex.nprocs > 1 && tree instanceof SequentialStructure) {
            System.err.println("ERROR: sequential data structure used with multiple threads... terminating.");
            System.exit(-1);
        }
        
        // prepare worker threads to run the trial
        startWallTime = new AtomicLong(0);
        startUserTime = new AtomicLong(0);
        CyclicBarrier start = new CyclicBarrier(ex.nprocs);
        ArrayList<Generator> arrays = ex.factory.getGenerators(ex, rng); // generators supply keys for each thread
        ArrayList<Worker> workers = new ArrayList<Worker>(ex.nprocs);    // these are the threads that perform random operations
        for (int i=0;i<ex.nprocs;i++) {
            workers.add(new TimedWorker<Integer>((long) (nseconds*1e9), arrays.get(i), ex, rng, tree, start, startUserTime, startWallTime, workers));
        }
        
        // perform garbage collection to clean up after the last trial, and record how much GC has happened so far
        System.gc();
        final long gcTimeStart = totalGarbageCollectionTimeMillis();
        
        // run the trial
        for (int i=0;i<ex.nprocs;i++) workers.get(i).start();
        ex.state = ExperimentState.RUNNING;
        long localStartTime = System.nanoTime();
        try {
            Thread.sleep((long)(nseconds * 1e3));
        } catch (InterruptedException ex1) {
            ex1.printStackTrace();
            System.exit(-1);
        }
        long localEndTime = System.nanoTime();
        ex.state = ExperimentState.STOPPED;

        // stop all threads and record how much GC has happened so far
        try { for (int i=0;i<ex.nprocs;i++) workers.get(i).join(); }
        catch (InterruptedException e) { e.printStackTrace(); System.exit(-1); }
        final long gcTimeEnd = totalGarbageCollectionTimeMillis();
        
        // compute key checksum for all threads (including from prefilling) and compare it with the key checksum for the data structure
        long threadsKeysum = pair.keysum;
        for (int i=0;i<ex.nprocs;++i) {
            threadsKeysum += workers.get(i).getKeysum();
        }
        //System.out.println("tree.supportsKeysum() = " + tree.supportsKeysum());
        if (tree.supportsKeysum()) {
            long dsKeysum = tree.getKeysum();
            if (dsKeysum != threadsKeysum) {
                throw new RuntimeException("threadsKeysum=" + threadsKeysum + " does not match dsKeysum=" + dsKeysum);
            } else {
                System.out.println("Key checksum validation PASSED (checksum=" + dsKeysum + ").");
                tree.debugPrint();
            }
        } else {
            // possibly add an unobtrusive warning that checksums are not computed for this data structure
            System.out.println("NOTICE: the data structure " + tree.getClass().getName() + " does not support key checksum validation.");
        }
        
        // produce output
        if (!discardResults) {
            long endWallTime = Long.MAX_VALUE;
            for (Thread t : workers) {
                Worker<Integer> w = (Worker<Integer>) t;
                if (w.getEndTime() < endWallTime) endWallTime = w.getEndTime();
            }
            
            double elapsed = (localEndTime - localStartTime)/1e9;
            out.print(prefix + ",");
            long ntruerq = 0, nfalserq = 0, ntruesnap = 0, nfalsesnap = 0, ntrueins = 0, nfalseins = 0, ntruedel = 0, nfalsedel = 0, ntruefind = 0, nfalsefind = 0;
            for (Worker w : workers) {
                ntrueins += w.getTrueIns();
                nfalseins += w.getFalseIns();
                ntruedel += w.getTrueDel();
                nfalsedel += w.getFalseDel();
                ntruefind += w.getTrueFind();
                nfalsefind += w.getFalseFind();
                ntruerq += w.getTrueRQ();
                nfalserq += w.getFalseRQ();
                ntruesnap += w.getTrueSnap();
                nfalsesnap += w.getFalseSnap();
            }
            int nnodes = 0;
            double averageDepth = 0;
            if (shouldMeasureTrees) {
                if (tree instanceof SetInterface) {
                    double depthSum = ((SetInterface) tree).getSumOfDepths();
                    nnodes = ((SetInterface) tree).sequentialSize();
                    averageDepth = (depthSum / nnodes);
                } // otherwise, we don't know what methods it has!
            }
            long ntrue = ntrueins+ntruedel+ntruefind+ntruerq+ntruesnap, nfalse = nfalseins+nfalsedel+nfalsefind+nfalserq+nfalsesnap;
            long nops = ntrue+nfalse;
            ex.throughput = (int)(nops/(double)elapsed);
            out.print(ex.nprocs + "," + nops + "," + ex.maxkey + ",");
            out.print(ex.ratio + ",");
            out.print(rng.nextInt() + "," + elapsed + ",");
            out.print(ntrueins + "," + nfalseins + "," + ntruedel + "," + nfalsedel + "," + ntruefind + "," + nfalsefind
                    + "," + ntruerq + "," + nfalserq + "," + ntruesnap + "," + nfalsesnap + "," + (-1) + "," + (-1)
                    + "," + ex.throughput + ",");
            out.print(averageDepth + ",");
            out.print((-1) + "," + (-1) + "," + nnodes + "," + pair.treeSize);
            out.print("," + ex.factory.getName());
            out.print(",unknown");
            
            // method (whether a data structure is using "put" or "put-if-absent" functionality)
            if (tree.getClass().getName().contains("Put")) {
                out.print(",put");
            } else out.print(",putIfAbsent");
            
            // experiment
            out.print("," + ex.ratio + "-" + (-1));
            
            // dummy spots, since metrics don't presently exist, but they do in old spreadsheets with pivotcharts...
            out.print(",,");
            
            // merged-experiment
            String mergedEx = ex.ratio.ins + "i-" + ex.ratio.del + "d-" + ex.maxkey;
            out.print("," + mergedEx);
            
            // row for legacy pivot charts
            String name = prefix.substring(0, prefix.indexOf(","));
            out.print("," + machine + "-" + name + "-" + mergedEx +"-"+ex.nprocs);
            
            // machine
            out.print("," + machine);
            
            // operations per thread (HARD LIMIT OF 128 FOR THESE VALUES; VALUES FOR HIGHER THREAD COUNTS WILL NOT BE PRINTED)
            for (Worker w : workers) {
                long ops = w.getTrueIns() + w.getFalseIns() + w.getTrueDel() + w.getFalseDel() + w.getTrueFind() +
                        w.getFalseFind() + w.getTrueRQ() + w.getFalseRQ() + w.getTrueSnap() + w.getFalseSnap();
                out.print(","+ops);
            }
            for (int i=workers.size();i<128;i++) out.print(",");
            
            // compute minimum starting times for all threads
            long minStartUserTime = Long.MAX_VALUE;
            long minStartWallTime = Long.MAX_VALUE;
            long minStartCPUTime = Long.MAX_VALUE;
            for (Worker w : workers) {
                minStartUserTime = Math.min(minStartUserTime, w.getMyStartUserTime());
                minStartWallTime = Math.min(minStartWallTime, w.getMyStartWallTime());
                minStartCPUTime = Math.min(minStartCPUTime, w.getMyStartCPUTime());
            }

            // user start+end times per thread
            for (Worker w : workers) {
                out.print(","+((w.getMyStartUserTime()-minStartUserTime)/1e9)+","+((w.getUserTime()-minStartUserTime)/1e9));
            }
            for (int i=workers.size();i<128;i++) out.print(",,");
            
            // wall start+end times per thread
            for (Worker w : workers) {
                out.print(","+((w.getMyStartWallTime()-minStartWallTime)/1e9)+","+((w.getWallTime()-minStartWallTime)/1e9));
            }
            for (int i=workers.size();i<128;i++) out.print(",,");

            // CPU start+end times per thread
            for (Worker w : workers) {
                out.print(","+((w.getMyStartCPUTime()-minStartCPUTime)/1e9)+","+((w.getCPUTime()-minStartCPUTime)/1e9));
            }
            for (int i=workers.size();i<128;i++) out.print(",,");
            // sizeOps
            for (Worker w : workers) {
                out.print(","+(w.getSizeDuration()/1e9)+","+w.getSizeOps());
            }
            for (int i=workers.size();i<128;i++) out.print(",,");


            // elapsed time per thread
            long totalElapsedUserTime = 0, totalElapsedWallTime = 0, totalElapsedCPUTime = 0;
            for (Worker w : workers) {
                totalElapsedUserTime += w.getUserTime()-w.getMyStartUserTime();
                totalElapsedWallTime += w.getWallTime()-w.getMyStartWallTime();
                totalElapsedCPUTime += w.getCPUTime()-w.getMyStartCPUTime();
            }
            
            // total elapsed times
            out.print(","+totalElapsedUserTime/1e9);
            out.print(","+totalElapsedWallTime/1e9);
            out.print(","+totalElapsedCPUTime/1e9);
            
            // garbage collection time and desired total elapsed time
            final double gcElapsedTime = (gcTimeEnd-gcTimeStart)/1e3;
            out.print(","+gcElapsedTime);
            out.print(","+nseconds);
            
            // total time for all threads in trial
            ex.totalThreadTime = (((totalElapsedCPUTime/1e9)+ 0 /*liveThreadsElapsedCPUTime*/)/ex.nprocs+gcElapsedTime);
            out.print(","+ex.totalThreadTime);
            
            if (PRINT_FREEMEM) {
                System.gc();
                final long freemem = Runtime.getRuntime().freeMemory();
                out.print("," + freemem + "," + (startFreemem - freemem) + "," + (nnodes > 0 ? ((startFreemem - freemem)/nnodes) : 0));
            }
            
            out.println(); // finished line of output
        }
        return true;
    }
    
    private long totalGarbageCollectionTimeMillis() {
        final List<GarbageCollectorMXBean> gcbeans = ManagementFactory.getGarbageCollectorMXBeans();
        long result = 0;
        for (GarbageCollectorMXBean gcbean : gcbeans) {
            result += gcbean.getCollectionTime();
        }
        return result;
    }   
    
    @Exclude
    protected static final class Ratio {
        final double del, ins, size;
        public Ratio(final double ins, final double del, final double size) {
            if (ins < 0 || del < 0 || size < 0 || ins+del+size > 1) throw new RuntimeException("invalid ratio " + ins + "i-" + del + "d");
            this.del = del;
            this.ins = ins;
            this.size = size;
        }
        @Override
        public String toString() { return "" + (int)(100*ins) + "i-" + (int)(100*del) + "d-" + (int)(100*size) + "s"; }
    }
    
    protected enum ExperimentState { PENDING, RUNNING, STOPPED }

    @Exclude
    public final class Experiment {
        volatile ExperimentState state = ExperimentState.PENDING;
        double totalThreadTime;
        final String alg, param;
        final int nprocs, maxkey;
        final Ratio ratio;
        final GeneratorFactory factory;
        int throughput; // exists to make access to this convenient so that we can decide whether we have finished warming up

        public Experiment(final String alg, final String param, final int nprocs, final int maxkey, final Ratio ratio, final GeneratorFactory factory) {
            this.alg = alg;
            this.param = param;
            this.nprocs = nprocs;
            this.maxkey = maxkey;
            this.ratio = ratio;
            this.factory = factory;
        }
        @Override
        public String toString() {
            return alg + param + "-" + nprocs + "thr-" + maxkey + "keys-" + ratio + (factory == null ? "null" : factory.getName());
        }
    }

    @Exclude
    public static class SwitchMap {
        private TreeMap<String, Double> backingMap;
        public SwitchMap() { backingMap = new TreeMap<String, Double>(); }
        public int size() { return backingMap.size(); }
        public void put(String key, Double val) { backingMap.put(key, val); }
        public double get(String key) {
            if (!backingMap.containsKey(key)) return 0;
            else return backingMap.get(key);
        }
        public String toString() {
            String s = "";
            boolean first = true;
            for (Entry<String, Double> e : backingMap.entrySet()) {
                s += (first ? "" : " ") + e.getKey() + "=" + e.getValue();
                first = false;
            }
            return s;
        }
    }

    @Exclude
    protected class DualPrintStream {
        private PrintStream stdout, fileout;
        public DualPrintStream(String filename) throws IOException {
            if (filename != null) {
                fileout = new PrintStream(new FileOutputStream(filename));
            }
            stdout = System.out;
        }
        public void print(double x) {
            print(String.valueOf(x));
        }
        public void println(double x) {
            println(String.valueOf(x));
        }
        public void print(String x) {
            stdout.print(x);
            if (fileout != null) fileout.print(x);
        }
        public void println(String x) {
            print(x + "\n");
        }
    }

    public class SizeKeysumPair {
        public final long treeSize;
        public final long keysum;
        public SizeKeysumPair(long treeSize, long keysum) {
            this.treeSize = treeSize;
            this.keysum = keysum;
        }
    }
    
    public class Pair<A, B> {
        public final A first;
        public final B second;

        public Pair(A first, B second) {
            super();
            this.first = first;
            this.second = second;
        }
    }
    
    SizeKeysumPair fillToSteadyState(
            final java.util.Random rand,
            final SetInterface<Integer> tree,
            Ratio ratio,
            int maxkey,
            final boolean showProgress) {

        long keysum = 0;
        
        // deal with an all-search workload by prefilling 50% insert, 50% delete
        // and normalize other ratios to have 100% updates.
        if (Math.abs(ratio.ins + ratio.del + ratio.size) < 1e-8) ratio = new Ratio(0.5, 0.5, 0);
        else ratio = new Ratio(ratio.ins / (ratio.ins+ratio.del+ratio.size), ratio.del / (ratio.ins+ratio.del+ratio.size), ratio.size/(ratio.ins+ratio.del+ratio.size));
        
        final int MAX_REPS = 200;
        final double THRESHOLD_PERCENT = 5; // must be within THRESHOLD_PERCENT percent of expected size to stop
        final int expectedSize = (int)(maxkey * (ratio.ins / (ratio.ins+ratio.del)) + 0.5);
        int treeSize = 0;
        int nreps = 0;
        long startFilling = System.nanoTime();

        int numThreads = 0;    // number of threads to use for prefilling phase
        int numOperations = 0; // number of operations to perform per thread in each iteration (up to MAX_REPS iterations)
        
        // first, check if this is a data structure incompatible with prefilling
        if (tree instanceof NoPrefillStructure) {
            throw new RuntimeException("Data structure type " + tree.getClass().getName() + " is not compatible with prefilling");
        }
        
        // unsynchronized data structures cannot prefill with many threads, and there is
        // no point pausing every few operations and checking the size of the tree,
        // so we use a simplified, single threaded prefilling method.
        // we also use this for data structures based on STM, since they typically
        // suffer many aborts under contention, and we want prefilling to be fast.
        if (tree instanceof SequentialStructure || tree instanceof STMStructure || tree instanceof SequentialPrefillStructure) {
            numThreads = 1;
            numOperations = maxkey / 10;
            
        // however, we prefill concurrent, non-STM data structures using many threads.
        } else {
            numThreads = Math.min(48, Runtime.getRuntime().availableProcessors() / 2);
            numOperations = 10 + maxkey/(2*numThreads);
        }

        // we prefill over several iterations (up to MAX_REPS iterations).
        // in each iteration, we spawn nthreads threads, which each try to
        // perform nOperations random operations according to the ratio
        // passed to this method.
        // as soon as one of these threads finishes performings its
        // nOperations operations, all threads will stop and wait on
        // a barrier, and the main thread will measure the size of the tree.
        // if it is within THRESHOLD_PERCENT percent of the expected size
        // in the steady state for this ratio, then we are done.
        // otherwise, we continue prefilling the tree.
        while (Math.abs(toPercent((double)treeSize / expectedSize) - 100) > THRESHOLD_PERCENT) {
            if (nreps++ > MAX_REPS) {
                System.out.println("WARNING: COULD NOT REACH STEADY STATE AFTER " + nreps + " REPETITIONS.");
                System.out.println("         treesize=" + treeSize + " expected=" + expectedSize + " percentToExpected=" + toPercent((double)treeSize / expectedSize) + " %diff=" + Math.abs(toPercent((double)treeSize / expectedSize) - 100) + " THRESHOLD_PERCENT=" + THRESHOLD_PERCENT);
                System.exit(-1);
            }

            final CyclicBarrier start = new CyclicBarrier(numThreads);
            final CyclicBarrier end = new CyclicBarrier(numThreads+1);
            final FixedNumberOfOpsWorker[] workers = new FixedNumberOfOpsWorker[numThreads];
            final BoolHolder done = new BoolHolder();
            for (int i=0;i<numThreads;i++) {
                workers[i] = new FixedNumberOfOpsWorker((AbstractAdapter) tree, numOperations, ratio, maxkey, new Random(rand.nextInt()), start, end, done); // FIX THIS HACKY CAST
            }
            for (int i=0;i<numThreads;i++) workers[i].start();
            try { end.await(); } catch (Exception ex) { ex.printStackTrace(); System.exit(-1); }
            treeSize = tree.size();
//                System.out.println(" treesize=" + treeSize + " expected=" + expectedSize + " percentToExpected=" + toPercent((double)treeSize / expectedSize) + " %diff=" + Math.abs(toPercent((double)treeSize / expectedSize) - 100) + " THRESHOLD_PERCENT=" + THRESHOLD_PERCENT);

            for (int i=0;i<numThreads;i++) {
                keysum += workers[i].getKeysum();
            }
        }
        
        long endFilling = System.nanoTime();
        System.out.print("initnodes-" + treeSize + "-");
        System.out.print("in" + toPercent((endFilling-startFilling) / 1e9 / 100) + "s["+nreps+"reps]-");
        return new SizeKeysumPair(treeSize, keysum);
    }
    
    protected ArrayList<Experiment> getExperiments() {
        final ArrayList<Experiment> exp = new ArrayList<Experiment>();
        GeneratorFactory gen = null;
        if (switches.get("generator") == Globals.GENERATOR_TYPE_DEFAULT) gen = new RandomGeneratorFactory();
        else if (switches.get("generator") == Globals.GENERATOR_TYPE_CHAINS) gen = new LeftRightChainGeneratorFactory(Globals.DEFAULT_CHAIN_SIZE);
        else {
            System.out.println("Critical error with generator selection...");
            System.exit(-1);
        }
        exp.add(new Experiment(alg, treeParam.toString(), nthreads, (int) switches.get("keyRange"), ratio, gen));
        return exp;
    }
    
    public void run(final PrintStream output) {
        // create output streams
        PrintStream out = output;
        if (out == null) {
            if (filename == null) {
                out = System.out;
            } else {
                try { out = new PrintStream(new File(filename)); }
                catch (Exception e) { e.printStackTrace(); System.exit(-1); }
            }
        }
        DualPrintStream stdout = null;
        try { stdout = new DualPrintStream(filename + "_stdout"); } catch (Exception e) { e.printStackTrace(); System.exit(-1); }
        
        // print header
        out.print("name"
                + ",trial"
                + ",nthreads"
                + ",threadops"
                + ",maxkey"
                + ",ratio"
                + ",seed"
                + ",time"
                + ",ninstrue"
                + ",ninsfalse"
                + ",ndeltrue"
                + ",ndelfalse"
                + ",nfindtrue"
                + ",nfindfalse"
                + ",nrqtrue"
                + ",nrqfalse"
                + ",nsnaptrue"
                + ",nsnapfalse"
                + ",rqsize"
                + ",snapsize"
                + ",throughput"
                + ",avgnodedepth"
                + ",noise"
                + ",balanceprob"
                + ",nnodes"
                + ",initnnodes"
                + ",factoryname"
                + ",helpedothers"
                + ",method"
                + ",experiment"
                + ",dummy,dummy"
                + ",merged-experiment"
                + ",row"
                + ",machine"
                );
        for (int i=0;i<128;i++) out.print(",thread"+i+"ops");
        for (int i=0;i<128;i++) out.print(",thread"+i+"userstart"+",thread"+i+"userend");
        for (int i=0;i<128;i++) out.print(",thread"+i+"wallstart"+",thread"+i+"wallend");
        for (int i=0;i<128;i++) out.print(",thread"+i+"cpustart"+",thread"+i+"cpuend");
        for (int i=0;i<128;i++) out.print(",thread"+i+"avgSizeDuration"+",thread"+i+"sizeOps");
        out.print(",totalelapsedusertime");
        out.print(",totalelapsedwalltime");
        out.print(",totalelapsedcputime");
        out.print(",gctime");
        out.print(",nseconds");
        out.print(",effectivetimeperthread");
        out.print(",restarted");
        out.println();
        
        // retrieve list of experiments to perform (this is a method because subclasses can implement it differently)
        ArrayList<Experiment> exp = getExperiments();

        // preview experiments, and determine now many runs there will be in total
        for (Experiment ex : exp) {
            System.out.println(ex);
        }
        System.out.println(exp.size() + " experiments in total");
        int numberOfRuns = exp.size() * ntrials;

        // start measuring time for the purpose of progress updates
        final long startTime = System.nanoTime();
        int nCompleted = 0;
        
        if (PRINT_FREEMEM) {
            System.gc();
            startFreemem = Runtime.getRuntime().freeMemory();
            System.out.println(" free memory: " + startFreemem);
        }        

        // perform the experiment
        Random rng = new Random((int) System.nanoTime()); // switches.get("seed"));
        for (Experiment ex : exp) {
            int experimentSeed = rng.nextInt(); //System.nanoTime();
            java.util.Random experimentRng = new java.util.Random(experimentSeed);

            // find appropriate factory to produce the tree we want for this trial
            // and run the trial
            for (TreeFactory factory : factories) if (ex.alg.equals(factory.getName())) {
                for (int trial=0;trial<ntrials;++trial) {
                    SetInterface<Integer> tree = factory.newTree(ex.param);
                    SizeKeysumPair p = new SizeKeysumPair(0, 0);
                    if (prefill) p = fillToSteadyState(experimentRng, tree, ex.ratio, ex.maxkey, false);
                    if (!runTrial(out, false, trial+1 == ntrials, factory.getName() + ex.param + "," + trial, p, experimentRng, (AbstractAdapter) tree, ex)) System.exit(-1); // TODO: FIX THE HACKY CAST...
                    progress(stdout, tree, ++nCompleted, trial, factory.getName(), startTime, numberOfRuns, ex);
                }
            }
        }
    }

    void progress(
            DualPrintStream stdout,
            final SetInterface<Integer> tree,
            int z,
            int i,
            String name,
            long startTime,
            int nRuns,
            Experiment ex) {
        
        double prog = ((int)(1000*(double) z / nRuns)) / 10.0;
        int left = (int)(((System.nanoTime()-startTime)/1e9)*(1-prog/100)/(prog/100));
        int elapsed = (int)((System.nanoTime()-startTime)/1e9+0.5);
        stdout.println(ex + " " + name + (tree.getClass().getName().contains("Put") ? "put" : "") + " trial " + i + " : " + prog + "% done, " + (left/60) + ":" + (left%60) + "s left, elapsed " + elapsed + "s");
    }
    
    double toPercent(double x) { // keep only 1 decimal point
        return Math.abs((int)(x*1000)/10.0);
    }

    public static void invokeRun(String[] args, final PrintStream output) {
        if (args.length < 4) {
            System.out.println("Insufficient command-line arguments.");
            System.out.println("Must include: #THREADS #TRIALS SECONDS_PER_TRIAL ALGORITHM");
            System.out.print("Valid algorithms are:");
            for (TreeFactory<Integer> f : factories) System.out.print(" " + f.getName());
            System.out.println();
            System.out.println("Can also include switches after mandatory arguments:");
            System.out.println("\t-chains   to insert/remove chains of 100 sequential integers, starting at random values");
            System.out.println("\t-s###     to set the random seed (32-bit signed int; default is " + Globals.DEFAULT_SEED + ")");
            System.out.println("\t-prefill  to prefill structures to steady state with random operations");
            System.out.println("\t-file-### to specify an output file to store results in");
            System.out.println("\t-param-## to provide a string parameter that will be passed to the tree factory");
            System.out.println("The following switches determine which operations are run (leftover % becomes search):");
            System.out.println("\t-ins%     to specify what % (0 to 100) of ops should be inserts");
            System.out.println("\t-del%     to specify what % (0 to 100) of ops should be deletes");
            System.out.println("\t-size%     to specify what % (0 to 100) of ops should be size");
            System.out.println("\t-keysM    random keys will be uniformly from range [0,M) (default 1000000)");
            System.exit(-1);
        }
        int nthreads = 0;
        int ntrials = 0;
        double nseconds = 0;
        String filename = null;
        String alg = "";
        boolean prefill = false;
        Object treeParam = "";

        SwitchMap switches = new SwitchMap();
        switches.put("seed", (double) Globals.DEFAULT_SEED);
        switches.put("generator", (double) Globals.GENERATOR_TYPE_DEFAULT);
        switches.put("keyRange", (double) Globals.DEFAULT_KEYRANGE);
        
        try {
            nthreads = Integer.parseInt(args[0]);
            ntrials = Integer.parseInt(args[1]);
            nseconds = Double.parseDouble(args[2]);
            alg = args[3];
        } catch (Exception ex) {
            System.out.println("NUMBER_OF_THREADS, NUMBER_OF_TRIALS, SECONDS_PER_TRIAL must all be numeric");
            System.exit(-1);
        }
        if (nthreads < 0 /*|| nthreads > THREAD_LIMIT*/) {
            System.out.println("Number of threads n must satisfy 0 <= n"/* <= " + THREAD_LIMIT + " (or else we'll crash MTL)"*/);
            System.exit(-1);
        }
        if (ntrials <= 0) {
            System.out.println("Must run at least 1 trial (recommended to run several and discard the first few)");
            System.exit(-1);
        }
        if (nseconds <= 0) {
            System.out.println("Number of seconds per trial s must satisfy 0 < s (should be at least a second, really)");
            System.exit(-1);
        }
        if (alg.length() == 0 || alg == null) {
            System.out.println("alg cannot be blank or null");
            System.exit(-1);
        }

        int totalOpPercent = 0;

        for (int i=0;i<args.length;i++) {
            if (args[i].startsWith("-")) {
                if (args[i].equals("-chains")) {
                    switches.put("generator", (double) Globals.GENERATOR_TYPE_CHAINS);
                } else if (args[i].matches("-seed[0-9]+")) {
                    try {
                        switches.put("seed", (double) Integer.parseInt(args[i].substring("-seed".length())));
                    } catch (Exception ex) {
                        System.out.println("Seed must be a 32-bit signed integer.");
                        System.exit(-1);
                    }
                } else if (args[i].matches("-ins[0-9]+(\\.[0-9]+){0,1}")) {
                    try {
                        switches.put("ratio-ins", Double.parseDouble(args[i].substring(4, args[i].length())));
                        totalOpPercent += switches.get("ratio-ins");
                        if (switches.get("ratio-ins") < 0) {
                            System.out.println("The insert percentage must be >= 0");
                            System.exit(-1);
                        }
                    } catch (Exception ex) {
                        System.out.println("The insert percentage must be a 32-bit integer.");
                        System.exit(-1);
                    }
                } else if (args[i].matches("-del[0-9]+(\\.[0-9]+){0,1}")) {
                    try {
                        switches.put("ratio-del", Double.parseDouble(args[i].substring(4, args[i].length())));
                        totalOpPercent += switches.get("ratio-del");
                        if (switches.get("ratio-del") < 0) {
                            System.out.println("The delete percentage must be >= 0");
                            System.exit(-1);
                        }
                    } catch (Exception ex) {
                        System.out.println("The delete percentage must be a 32-bit integer.");
                        System.exit(-1);
                    }
                }else if (args[i].matches("-size[0-9]+(\\.[0-9]+){0,1}")) {
                    try {
                        switches.put("ratio-size", Double.parseDouble(args[i].substring(5, args[i].length())));
                        totalOpPercent += switches.get("ratio-size");
                        if (switches.get("ratio-size") < 0) {
                            System.out.println("The size percentage must be >= 0");
                            System.exit(-1);
                        }
                    } catch (Exception ex) {
                        System.out.println("The size percentage must be a 32-bit integer.");
                        System.exit(-1);
                    }
                } else if (args[i].matches("-keys[0-9]+")) {
                    try {
                        switches.put("keyRange", (double) Integer.parseInt(args[i].substring(5, args[i].length())));
                        if (switches.get("keyRange") < 1) {
                            System.out.println("The key range must be > 0");
                            System.exit(-1);
                        }
                    } catch (Exception ex) {
                        System.out.println("The key range must be a 32-bit integer.");
                        System.exit(-1);
                    }
                } else if (args[i].startsWith("-param-")) {
                    treeParam = args[i].substring("-param-".length());
                } else if (args[i].startsWith("-file-")) {
                    filename = args[i].substring("-file-".length());
                } else if (args[i].matches("-prefill")) {
                    prefill = true;
                } else {
                    System.out.println("Unrecognized command-line switch: \"" + args[i] + "\"");
                    System.exit(-1);
                }
            }
        }

        if (totalOpPercent > 100) {
            System.out.println("Total percentage over all operations cannot exceed 100");
            System.exit(-1);
        }

        boolean found = false;
        for (TreeFactory<Integer> f : factories) {
            String name = f.getName();
            if (name.equals(alg)) {
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Algorithm \"" + alg + "\" was not recognized.");
            System.out.println("Run this class with no arguments to see a list of valid algorithms.");
            System.exit(-1);
        }

        (new Main(nthreads, ntrials, nseconds, filename,
                new Ratio(switches.get("ratio-ins") / 100., switches.get("ratio-del") / 100., switches.get("ratio-size") / 100.),//, switches.get("ratio-rq") / 100., switches.get("ratio-snap") / 100.),
                alg, switches, prefill, treeParam)).run(output);
    }

    public static void main(String[] args) throws Exception {
        invokeRun(args, null);
    }
}
