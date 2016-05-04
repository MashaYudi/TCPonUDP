package edu.spbspu.dcn.netcourse.UDP.RightSide;

import edu.spbspu.dcn.netcourse.UDP.ByteHandler;
import edu.spbspu.dcn.netcourse.UDP.Common.ReceiverListener;
import edu.spbspu.dcn.netcourse.UDP.Common.Sender;
import edu.spbspu.dcn.netcourse.UDP.Common.SenderListener;
import edu.spbspu.dcn.netcourse.UDP.packet.DataPacket;
import edu.spbspu.dcn.netcourse.concurrentUtils.Channel;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Created by masha on 22.12.15.
 */
public class RightSlideWindowController implements ReceiverListener, SenderListener {

    private final ByteHandler byteHandler;
    private final Sender sender;
    private final int swSize;
    private boolean isFinished;
    private int neededInd = 0;
    private int lastInd = -10;
    private final HashMap<Integer, DataPacket> packetMap = new HashMap<>();

    public RightSlideWindowController(ByteHandler byteHandler, InetAddress address, int port, int packetSize, int swSize) throws SocketException {
        this.byteHandler = byteHandler;
        this.swSize = swSize;
        this.isFinished = false;
        this.sender = new Sender(this, new Channel<DataPacket>(swSize), new DatagramSocket(), address, port);
    }

    @Override
    public void onReceive(DataPacket packet) {
        if(packet.getIndex() - neededInd >=swSize)
            return;
        System.out.println("recieved packet index = " + packet.getIndex());
        if(packet.getIndex() == 128)
            System.out.println("that thing!");
        System.out.println("number: " + Math.abs(packet.getIndex()));
        sender.put(new DataPacket(Math.abs(packet.getIndex()), new byte[0]));
        if(packet.getIndex() >= 0 && (packetMap.get(packet.getIndex()) !=null || packet.getIndex() < neededInd))
            return;
        else {
            packetMap.put(packet.getIndex(), packet);
        }
        if(packet.getIndex()< 0){
            lastInd = -packet.getIndex() - 1;
            isFinished = true;
           // sender.stop();
        }
        if(lastInd == neededInd - 1){
            System.out.println("senderstop1");
            sender.stop();
            byteHandler.onComplete();
        }
        if(packet.getIndex() == neededInd)
            while(!packetMap.isEmpty()) {
                if (neededInd != lastInd)
                    //System.out.println("i wrote" + packetMap.get(neededInd).getIndex());
                    byteHandler.handle(packetMap.get(neededInd).getData());
                packetMap.remove(neededInd);
                neededInd++;
            }
                if (isFinished && lastInd == neededInd + 1 ) {
                    System.out.println("senderstop2");
                    sender.stop();
                    byteHandler.onComplete();
                }

    }

    @Override
    public boolean isComplete() {
        return isFinished;
    }

    @Override
    public void onSend(int index, long timeOfSend) { //JUST NOTHING

    }

    public void start(){
        sender.start();
    }

    public void stop(){
        sender.stop();
    }

}
