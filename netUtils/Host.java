package edu.spbspu.dcn.netcourse.netUtils;

import edu.spbspu.dcn.netcourse.concurrentUtils.Channel;
import edu.spbspu.dcn.netcourse.concurrentUtils.Task;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by masha on 18.09.15.
 */
public class Host implements Runnable {
    private ServerSocket ss;
    private final int maxSessionCount;
    private volatile int sessionCount = 0;
    private final Object lock = new Object();
    private final Channel<Task> channel;
    private final MessageHandlerFactory messageHandlerFactory;
    private volatile boolean isActive;
    private Thread thread;

    public Host(int port, int maxSessionCount, Channel<Task> channel, MessageHandlerFactory messageHandlerFactory) {
        this.maxSessionCount = maxSessionCount;
        this.messageHandlerFactory = messageHandlerFactory;
        try {
            this.ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.channel = channel;
    }

    public void start() {
        thread = new Thread(this);
        if (isActive)
            throw new IllegalStateException("WHY ARE YOU DOING THIS?!");
        this.isActive = true;
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (isActive) {
                Socket socket = ss.accept();
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                if (sessionCount == maxSessionCount) {
                    dos.writeUTF("Sorry, server too busy. Please try later");
                    if (socket != null)
                        socket.close();
                    continue;
                }
                dos.writeUTF("Session open");
                sessionCount++;
                channel.put(new Session(socket, this, messageHandlerFactory.create()));
            }
        } catch (IOException e) {
            if("Socket closed".equals(e.getMessage())) {
                if (!isActive)
                    return;
                else
                    System.out.println("SOCKET PANIC");
            }

        } finally {
            stop();
        }
    }

    public void sessionClose() {
        synchronized (lock) {
            sessionCount--;
        }
    }

    public void stop() {
        if (isActive) {
            isActive = false;
            thread.interrupt();
            try {
                if(!ss.isClosed())
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
