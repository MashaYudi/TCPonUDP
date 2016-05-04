package edu.spbspu.dcn.netcourse.UDP.RightSide;

import edu.spbspu.dcn.netcourse.UDP.Common.Receiver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by masha on 22.12.15.
 */
public class RightMain {
    public static void main(String[] args){
        final RightSlideWindowController rswc;
        final Receiver receiver;
        final DiskWriter diskWriter;

        //0 path
        //1 inetAdress
        //2 senderport
        //3 receiverPort
        //4 packetSize
        //5 swSize

        try{
            diskWriter = new DiskWriter(args[0], Integer.valueOf(args[4]/*packetSize*/)-4);
            rswc = new RightSlideWindowController(diskWriter, InetAddress.getByName(args[1]), Integer.valueOf(args[3])/*port_sender*/,
                    Integer.valueOf(args[4])/*packetSize*/,Integer.valueOf(args[5])/*swSize*/);
            receiver = new Receiver(rswc,  InetAddress.getByName(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[4]));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        receiver.start();
        rswc.start();
        diskWriter.start();
        Runtime.getRuntime().addShutdownHook(new Thread((Runnable)() -> {
            receiver.stop();
            rswc.stop();
            diskWriter.stop();
        }));

    }
}
