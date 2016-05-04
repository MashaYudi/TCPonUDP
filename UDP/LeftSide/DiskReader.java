package edu.spbspu.dcn.netcourse.UDP.LeftSide;

import edu.spbspu.dcn.netcourse.UDP.ByteHandler;
import edu.spbspu.dcn.netcourse.UDP.Reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by masha on 04.12.15.
 */
public class DiskReader implements Reader {

    private FileInputStream fis;
    //private int packetSize;
    byte[] buffer;

    private final ByteHandler byteHandler;
    private Thread thread;
    private boolean isActive;


    public DiskReader(String path, Integer size, ByteHandler swc){
       // this.packetSize = size;
        byteHandler = swc;
        buffer = new byte[size];
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void start(){
        isActive = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        if(!isActive){
            throw new IllegalStateException("NO!");
        }
        isActive = false;
        try {
            fis.close();
            //byteHandler.onComplete();

        } catch (IOException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }
    public void finish() throws IOException {
        byteHandler.onComplete();
        fis.close();
    }

    @Override
    public void run() {
        try {
            while (isActive) {
                int length = fis.read(buffer, 0, buffer.length);
                if (length == -1) {
                    finish();
                    return;
                }
                byteHandler.handle(buffer);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void read() {
    }
}
