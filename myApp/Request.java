package edu.spbspu.dcn.netcourse.myApp;

import java.io.*;

/**
 * Created by masha on 27.11.15.
 */
public class Request implements Runnable{

    DataOutputStream dos;
    DataInputStream dis;
    private volatile boolean isActive;
    private Thread thread;
    String line = "";
    Client.Stuff stuff;

    public Request(DataOutputStream dos, DataInputStream dis, Client.Stuff stuff){
        this.dis = dis;
        this.dos = dos;
        this.stuff = stuff;
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

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try{
        while(isActive && (!"Sorry, something wrong with server".equals(line))){
            dos.writeUTF("veRyDiffiCULtWordToBEWRITTTenBYUSer");
            line = dis.readUTF();
            System.out.print(line);
            Thread.sleep(4000);
        }
           stuff.clientStop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isActive(){
        return isActive;
    }
    public void stop() {
        if (isActive) {
            isActive = false;
        }
        thread.interrupt();
    }
}
