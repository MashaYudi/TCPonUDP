package edu.spbspu.dcn.netcourse.concurrentUtils;

import java.util.LinkedList;

/**
 * Created by masha on 02.10.15.
 */
public class Channel<T> {
    private final LinkedList<Object> queue = new LinkedList<Object>();
    private final int maxObjects;
    private final Object lock = new Object();

    public Channel(int maxObjects) {
        this.maxObjects = maxObjects;
    }

    public void put(T obj) {
        if (obj == null)
            throw new IllegalArgumentException("must be not null");
        synchronized (lock) {
            while (queue.size() == maxObjects)
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            queue.addLast(obj);
            lock.notifyAll();
        }
    }

    public T get() throws InterruptedException {
        synchronized (lock) {
            while (queue.isEmpty()) {
                lock.wait();
            }
            return (T) queue.removeFirst();
        }
    }

    public T take() throws InterruptedException {
        synchronized (lock){
            while (queue.isEmpty()){

                    lock.wait();

            }
            return (T) queue.getFirst();
        }
    }
    public int size() {
        synchronized (lock) {
            return queue.size();
        }
    }
}
