package edu.spbspu.dcn.netcourse.UDP.LeftSide;

import edu.spbspu.dcn.netcourse.UDP.*;
import edu.spbspu.dcn.netcourse.UDP.Common.ReceiverListener;
import edu.spbspu.dcn.netcourse.UDP.Common.Sender;
import edu.spbspu.dcn.netcourse.UDP.Common.SenderListener;
import edu.spbspu.dcn.netcourse.UDP.Common.Times;
import edu.spbspu.dcn.netcourse.UDP.packet.DataPacket;
import edu.spbspu.dcn.netcourse.concurrentUtils.Channel;

import java.io.FileNotFoundException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by masha on 04.12.15.
 */
public class LeftSlideWindowController implements ReceiverListener, ByteHandler, SenderListener {

    private final int secToMillisec = 1000;
    private volatile boolean flag;
    private boolean isEOF;
    private boolean completed;
    private final Sender sender;
    private final HashMap<Integer, byte[]> packetMap;
    private final int swSize;
    private final int packetSize;
    private final Channel<Times> timeMap;
   // private final Map<Integer,Long> timeMap;
    private final LinkedList<Integer> indexes;
    private final DiskReader diskReader;
    private Integer firstIndex;
    private Integer lastIndex;
   // private Timer timer;
    private volatile TimeOut timer;
   // private TimerTask timeOutTask;
    private long timeOutSec;
    private long minTime;
    private final Object lock = new Object();

   public LeftSlideWindowController(String path, InetAddress address, int port, int packetSize, int swSize, int timeout) throws SocketException, FileNotFoundException {
       this.swSize = swSize;
       this.packetSize = packetSize;
       this.sender = new Sender(this, new Channel<DataPacket>(swSize), new DatagramSocket(), address, port);
       this.timeOutSec = timeout;
       this.diskReader = new DiskReader(path, packetSize - 4, this);
       this.timer = new TimeOut(this);
       packetMap = new HashMap<Integer, byte[]>();
       timeMap = new Channel<>(swSize+6);
       //timeMap = new ConcurrentHashMap<Integer, Long>();
       lastIndex = -1;
       firstIndex = 0;
       isEOF = false;
       completed = false;
       indexes = new LinkedList<>();
       flag = false;
   }

    public void timeOutTask(){
        System.out.println("resend");
        synchronized (lock) {
            if (packetMap.isEmpty()) {
                timer.confirm();
                return;
            }
            flag = true;
            if(firstIndex < 0)
                System.out.println("123");
            sender.put(new DataPacket(firstIndex, packetMap.get(firstIndex)));
            timer.launch((timeOutSec+1)*1000);
        }
    }

    public void start() {
        //System.out.println("lswc on");
        sender.start();
        diskReader.start();
        timer.start();
    }

    public void stop() {
         diskReader.stop();
        sender.stop();
        timer.stop();
    }

    @Override
    public void onReceive(DataPacket dataPacket) {
        synchronized (lock) {
            if (!packetMap.containsKey(dataPacket.getIndex()))
                return;

        packetMap.remove(dataPacket.getIndex());
        //System.out.println("packets: " + packetMap.size() + "  timeMap size: " + timeMap.size() + "  confirmed: " + indexes.size());

        if(firstIndex != dataPacket.getIndex()){
            indexes.addLast(dataPacket.getIndex());
        }
        else {
            try {
                timer.confirm();
                timeMap.get();
                firstIndex++;

                while (indexes.contains(firstIndex)) {
                    timeMap.get();
                    firstIndex++;
                }

                if (!(timeMap.size() == 0)) {
                    timer.launch(timeMap.take().getTime() + timeOutSec * 1000 - System.currentTimeMillis());
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

                lock.notify();
            }

        if (isEOF && packetMap.isEmpty()) {
            timer.confirm();
            timer.stop();
            sender.stop();
            completed = true;
        }
    }

    @Override
    public boolean isComplete() {
        return completed;
    }

    @Override
    public void onComplete() {
        System.out.println("empty stuff");
        isEOF = true;
        lastIndex++;
        handle(new byte[0]);
        //sender.put(new DataPacket(-lastIndex,new byte[0]));
    }

    @Override
    public void handle(byte[] bytes) {
        lastIndex++;
        byte[] packetData = Arrays.copyOf(bytes, bytes.length);
        synchronized (lock) {
        while (packetMap.size() >= swSize)
            try {
                    lock.wait();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        packetMap.put(lastIndex, packetData);

        if(!isEOF)
            sender.put(new DataPacket(lastIndex, packetData));
        else
            sender.put(new DataPacket(-lastIndex, packetData));
        }
    }

    @Override
    public void onSend(int index, long timeOfSend) {

        if (timer.isSleep()) {
            timer.launch(timeOfSend + timeOutSec*1000 - System.currentTimeMillis());
        }
        if(flag){
            flag = false;
            return;
        }
        timeMap.put(new Times(index, timeOfSend));
    }

}
