package edu.spbspu.dcn.netcourse.concurrentUtils;

import java.util.LinkedList;

/**
 * Created by masha on 16.10.15.
 */

/** todo
 *  add InitSize,
 *  MaxSize,
 *  resourceIncrement
 *  resourceDecrement
 *  periodOfSharing*/
public class ThreadPool {
    private final LinkedList<WorkerThread> allWorkers = new LinkedList<WorkerThread>();
    private final Channel<Runnable> freeWorkers;
    private final Object lock = new Object();
    private final int maxSize;
    private final int resourceInc;
    private int currentSize;

    public ThreadPool(int initSize ,int maxSize, int resourceInc) {
        WorkerThread worker;
        this.maxSize = maxSize;
        this.currentSize = initSize;
        this.resourceInc = resourceInc;
        freeWorkers = new Channel<Runnable>(maxSize);
        for (int i = 0; i < initSize; i++) {
            worker = new WorkerThread(this, i);
            allWorkers.addLast(worker);
            freeWorkers.put(worker);
        }
    }
    public void execute(Task task) throws InterruptedException {
        if (task == null)
            throw new IllegalArgumentException("must be not null");
        if (freeWorkers.size() <= 0)
            synchronized (lock) {
                if (maxSize > currentSize)
                    for (int i = 0; i < resourceInc; i++) {
                        if (currentSize == maxSize)
                            break;
                        WorkerThread worker = new WorkerThread(this, currentSize);
                        allWorkers.addLast(worker);
                        freeWorkers.put(worker);
                        currentSize++;
                    }
            }
        ((WorkerThread) freeWorkers.get()).execute(task);
    }
    void onTaskCompleted(WorkerThread workerThread) {
        freeWorkers.put(workerThread);
    }

    public void stop() {
        for(WorkerThread worker : allWorkers) {
            System.out.println("worker stop");
            worker.stop();
        }
    }
}
