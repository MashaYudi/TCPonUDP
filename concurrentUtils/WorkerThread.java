package edu.spbspu.dcn.netcourse.concurrentUtils;

/**
 * Created by masha on 16.10.15.
 */
public class WorkerThread implements Task {
    private final Thread thread;
    private final ThreadPool boss;
    private final Object lock = new Object();
    private volatile boolean isActive;
    private Task currentTask;

    public WorkerThread(ThreadPool boss, int id) {
        this.boss = boss;
        this.thread = new Thread(this, "WorkerThread_" + Integer.toString(id));
        this.isActive = true;
        thread.start();
    }

    @Override
    public void run() {
        synchronized (lock) {
            while (isActive) {
                while (currentTask == null)
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        if (!isActive)
                            return;
                    }
                try {
                    if (!isActive)
                        return;
                    currentTask.run();
                } finally {
                    currentTask = null;
                    boss.onTaskCompleted(this);
                }
            }
        }
    }

    public void execute(Task task) {
        if (task == null)
            throw new IllegalArgumentException("must be not null");
        synchronized (lock) {
            if (currentTask != null)
                throw new IllegalStateException("PANIC");
            currentTask = task;
            lock.notify();
        }
    }


    @Override
    public void stop() {
        isActive = false;
        thread.interrupt();
        if(currentTask != null) {
            System.out.println("task stop");
            currentTask.stop();
        }
    }
}
