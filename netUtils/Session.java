package edu.spbspu.dcn.netcourse.netUtils;

import edu.spbspu.dcn.netcourse.concurrentUtils.Task;
import edu.spbspu.dcn.netcourse.myApp.MyMessageHandler;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by masha on 18.09.15.
 */
public class Session implements Task {
    private final Host host;
    private final Socket socket;
    private final MessageHandler messageHandler;
    //private Thread thread;

    public Session(Socket socket, Host host, MessageHandler messageHandler) {
        this.socket = socket;
        this.host = host;
        this.messageHandler = messageHandler;
    }

   /* public void start() {
        thread = new Thread(this);
        thread.start();
    }*/

    @Override
    public void run() {
        try {
            if((socket == null)|| socket.isClosed())
                return;
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            System.out.println(socket.getLocalPort());
            String message = dis.readUTF();
            while (!"exit".equalsIgnoreCase(message)) {
                dos.writeUTF(messageHandler.handle(message));
                message = dis.readUTF();
            }
            System.out.println(Thread.currentThread().getName() + " closed");
        } catch (SocketException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            host.sessionClose();
        }
    }

    @Override
    public void stop() {
        if (socket != null) {
            try {
                if(!socket.isClosed()) {
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeUTF("Sorry, something wrong with server");
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}