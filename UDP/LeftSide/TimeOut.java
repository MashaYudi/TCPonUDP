package edu.spbspu.dcn.netcourse.UDP.LeftSide;


import edu.spbspu.dcn.netcourse.concurrentUtils.Task;

public class TimeOut implements Task {
    private final LeftSlideWindowController swsc;
    private volatile long sleepTime;
    private volatile boolean isConfirm;
    private Thread thread;
    private volatile boolean isActive;
    private final Object lock = new Object();

    public TimeOut(LeftSlideWindowController swsc) {
        this.swsc = swsc;
        this.isActive = false;
        this.isConfirm = true;

    }
    public void start() {
        isActive = true;
        thread = new Thread(this);
        thread.start();
    }

    public boolean isSleep() {
        return isConfirm;
    }

    public void confirm() {
        isConfirm = true;
    }

    public void launch(long sleepTime) {
        this.sleepTime = sleepTime;
        this.isConfirm = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void run() {
        while (isActive) {
            try {
                while (isConfirm)
                    synchronized (lock) {
                        lock.wait();
                    }

                if (sleepTime > 0)
                    Thread.sleep(sleepTime);
                if (!isConfirm)
                    swsc.timeOutTask();
            } catch (InterruptedException e) {
                if (!isActive)
                    return;
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        isActive = false;
        isConfirm = false;
        thread.interrupt();
    }
}