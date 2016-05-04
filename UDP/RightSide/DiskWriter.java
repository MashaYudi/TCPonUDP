package edu.spbspu.dcn.netcourse.UDP.RightSide;

import edu.spbspu.dcn.netcourse.UDP.ByteHandler;
import edu.spbspu.dcn.netcourse.UDP.packet.DataPacket;
import edu.spbspu.dcn.netcourse.concurrentUtils.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by masha on 04.12.15.
 */
public class DiskWriter implements ByteHandler, Task{

    private final ConcurrentLinkedQueue<DataPacket> queue = new ConcurrentLinkedQueue<>();
    private FileOutputStream fos;
    private volatile boolean isActive;
    public Thread thread;
    private final Object lock = new Object();

    public DiskWriter(String filename, Integer packetSize) throws IOException {
            File f = new File(filename);
            f.createNewFile();
            fos = new FileOutputStream(f);
    }

    public void start(){
        thread = new Thread(this);
        isActive = true;
        thread.start();
    }

    public void stop(){
        isActive = false;
        while(queue.size()>0)
            queue.poll();
        thread.interrupt();
        try{
            fos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void handle(byte[] data) {
        queue.add(new DataPacket(0, data));
        synchronized (lock){
            lock.notify();
        }
    }

    @Override
    public void onComplete() {
        isActive = false;
        thread.interrupt();}

    @Override
    public void run() {
        try{
            int i = 0;
            while (isActive || queue.size() > 0){
                synchronized (lock){
                    while(queue.size()<=0) {
                        if(!isActive)
                            return;
                        lock.wait();
                    }
                    //DataPacket dpack = queue.poll();
                    fos.write(queue.poll().getData());
                }
            }
        } catch (InterruptedException | IOException e) {
            if(!isActive)
                return;
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
