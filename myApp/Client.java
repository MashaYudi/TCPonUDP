package edu.spbspu.dcn.netcourse.myApp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by masha on 11.09.15.
 */

public class Client {

    public static class Stuff{
        Boolean isClientActive = true;

        void clientStop(){
            isClientActive = false;
        }

        boolean isClientActive(){
            return isClientActive;
        }
    }

    static volatile Boolean isActive = true;
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: address port");
            return;
        }
        int port = 0;
        final Stuff stuff = new Stuff();
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Usage integer for port");
        }
        InetAddress address;
        try {
            address = InetAddress.getByName(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Socket socket = null;
        try {
            socket = new Socket(address, port);
            System.out.println(socket.getLocalPort());
            final DataInputStream dis = new DataInputStream(socket.getInputStream());
            System.out.println(dis.readUTF());

            final DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            final Request request = new Request(dos,dis, stuff);
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (stuff.isClientActive())
                            dos.writeUTF("exit");
                        if(request.isActive())
                            request.stop();
                        stuff.clientStop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }}));

            String line;
            System.out.println("hi! What's your name?");
            line = bufferedReader.readLine();
            dos.writeUTF(line);
            System.out.print(dis.readUTF());
            dos.writeUTF("SuperRequest");
            line = dis.readUTF();
            System.out.print(line);

            request.start();
            do {
                line = bufferedReader.readLine();
                dos.writeUTF(line);
                line = dis.readUTF();
                System.out.print(line);
            } while ((!"Sorry, something wrong with server".equalsIgnoreCase(line)) &&(stuff.isClientActive()));
            isActive = false;
            System.out.print("catched");
            request.stop();
        }
        catch (EOFException e) {
           System.out.println("Sorry, something wrong with server");
           if(!isActive) {
               return;
           }}
        catch (SocketException e) {
            if (!isActive) {
                return;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        finally
         {
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

}

