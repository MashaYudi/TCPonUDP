package edu.spbspu.dcn.netcourse.concurrentUtils;

/**
 * Created by masha on 02.10.15.
 */
public class Dispatcher implements Runnable {
    private final Channel<Task> channel;
    private final ThreadPool threadPool;
    private volatile boolean isActive;
    private Thread thread;

    public Dispatcher(Channel<Task> channel, ThreadPool threadPool) {
        this.channel = channel;
        this.threadPool = threadPool;
    }

    public void start() {
        thread = new Thread(this);
        isActive = true;
        thread.start();
    }

    @Override
    public void run() {
        while (isActive) {
            try {
                threadPool.execute(channel.get());
            } catch (InterruptedException e) {
                if (!isActive)
                    return;
            }
        }
    }

    public void stop() {
        if (isActive) {
            isActive = false;
            threadPool.stop();
            while (channel.size() > 0)
                try {
                    channel.get().stop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            thread.interrupt();
        }
    }
}
