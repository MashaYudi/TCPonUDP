package edu.spbspu.dcn.netcourse.UDP.Common;

import edu.spbspu.dcn.netcourse.UDP.packet.DataPacket;
import edu.spbspu.dcn.netcourse.concurrentUtils.Channel;
import edu.spbspu.dcn.netcourse.concurrentUtils.Task;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Created by masha on 04.12.15.
 */
public class Sender implements Task {
    DatagramSocket datagramSocket;
    DataPacket dataPacket;
    private InetAddress address;
    private int port;
    SenderListener senderListener;
    private final Channel<DataPacket> channel;
    private volatile boolean isActive;
    public Thread thread;

    public Sender(SenderListener senderListener, Channel<DataPacket> channel, DatagramSocket datagramSocket, InetAddress address, int port) {
        this.channel = channel;
        this.senderListener = senderListener;
        this.datagramSocket = datagramSocket;
        this.port = port;
        this.address = address;
    }

    public void put(DataPacket packet) {
        channel.put(packet);
    }

    public void start(){
        thread = new Thread(this);
        isActive = true;
        thread.start();
    }

    @Override
    public void run() {
        try {
        while (isActive) {
            //if(channel.size() == 0)
                //System.out.println("channel is empty");
            dataPacket = channel.get();
            if(dataPacket.getIndex()<0)
                System.out.println("less!");
            DatagramPacket datagramPacket = dataPacket.toDgP();
            datagramPacket.setPort(port);
            datagramPacket.setAddress(address);
            datagramSocket.send(datagramPacket);

            System.out.println("Send: " + dataPacket.getIndex());
            senderListener.onSend(dataPacket.getIndex(), System.currentTimeMillis());}
        } catch (IOException | InterruptedException e) {
            if(!isActive) {
                return;
            }
            e.printStackTrace();
        } finally{
            if(!datagramSocket.isClosed()){
                datagramSocket.disconnect();
                datagramSocket.close();
            }
        }
    }

    @Override
    public void stop() {
        System.out.println("sender stopped");
        if(!isActive)
            return;
        isActive = false;
        thread.interrupt();
        if(!datagramSocket.isClosed()){
            datagramSocket.disconnect();
            datagramSocket.close();
        }
    }
}
