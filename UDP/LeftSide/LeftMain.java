package edu.spbspu.dcn.netcourse.UDP.LeftSide;

import edu.spbspu.dcn.netcourse.UDP.Common.Receiver;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by masha on 21.12.15.
 */
public class LeftMain {
    public static void main(String[] args){
        Receiver receiver;
        LeftSlideWindowController lswc;

        //0 path
        //1 inetAdress
        //2 senderport
        //3 receiverPort
        //4 packetSize
        //5 swSize
        //6 timeOut

        try {
            lswc = new LeftSlideWindowController(args[0], InetAddress.getByName(args[1]), Integer.valueOf(args[2]),
                    Integer.valueOf(args[4]), Integer.valueOf(args[5]),Integer.valueOf(args[6]));
            receiver = new Receiver(lswc,InetAddress.getByName(args[1]),Integer.valueOf(args[3]),Integer.valueOf(args[4]));
        } catch (UnknownHostException | SocketException | FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        receiver.start();
        lswc.start();

        Runtime.getRuntime().addShutdownHook(new Thread((Runnable)() -> {
            receiver.stop();
            lswc.stop();
        }));
    }
}