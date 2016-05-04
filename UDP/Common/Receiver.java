package edu.spbspu.dcn.netcourse.UDP.Common;

import edu.spbspu.dcn.netcourse.UDP.packet.DataPacket;
import edu.spbspu.dcn.netcourse.concurrentUtils.Task;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Created by masha on 04.12.15.
 */
public class Receiver implements Task {
    private final ReceiverListener resListener;
    private final DatagramSocket dSocket;
    private final byte[] bytes;
    private volatile boolean isActive;
    private Thread thread;

    public Receiver(ReceiverListener resListener, InetAddress address, int port, int packetSize) throws SocketException {
        this.resListener = resListener;
        this.bytes = new byte[packetSize];
        this.dSocket = new DatagramSocket(port);
    }

    public void start() {
        //System.out.println("reciever on");
        thread = new Thread(this);
        isActive = true;
        thread.start();
    }

    @Override
    public void stop() {
        if (!isActive)
            return;
        //throw new IllegalStateException("NO!");
        isActive = false;
        thread.interrupt();
        if (dSocket.isClosed()) {
            dSocket.disconnect();
            dSocket.close();
        }
    }

    @Override
    public void run() {
        try {
            while (isActive) {
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                DataPacket dataPacket = new DataPacket(0, bytes);
                dSocket.receive(packet);
                resListener.onReceive(dataPacket.toDP(packet));
                if (resListener.isComplete()) {
                    stop();
                }
            }
        }catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            if(!isActive)
                return;
            e.printStackTrace();
        } finally{
            if(!dSocket.isClosed()){
                dSocket.disconnect();
                dSocket.close();
            }
        }
       /* System.out.println("receiver run");
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        DataPacket dataPacket = new DataPacket(0,bytes);
        while (true) {
            try {
                dSocket.receive(packet);
               // System.out.println("received" + Arrays.toString(packet.getData()));
                resListener.onReceive(dataPacket.toDP(packet));
                //if(resListener.isComplete()){
                  //  stop();
                //}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
    }
}
