package edu.spbspu.dcn.netcourse.UDP.packet;

import java.net.DatagramPacket;

/**
 * Created by masha on 05.12.15.
 */
public class DataPacket {

    private int index;
    private byte[] data;

    public DataPacket(int index, byte[] data){
        this.index = index;
        this.data = data;
    }


    public int size(){
        return data.length;
    }

    public int getIndex(){
        return this.index;
    }

    public byte[] getData(){ return this.data;}

    public DatagramPacket toDgP() {
        //if(data == null)
          //  System.out.println("NULL DATA WHAT TO DO NOW" + index);
        byte[] result = new byte[data.length+4];
        result[0] = (byte) (index >> 24);
        result[1] = (byte) (index >> 16);
        result[2] = (byte) (index >> 8);
        result[3] = (byte) (index);
        if(data.length>0)
        for(int i = 0; i < data.length; i++){
            result[i+4] = data[i];
        }
        return new DatagramPacket(result, result.length);
    }

    public DataPacket toDP(DatagramPacket datagramPacket){
        int index;
        byte bytes[] = datagramPacket.getData();
        index =  bytes[0]<< 24 | (bytes[1] & 0xFF)<< 16 | (bytes[2] & 0xFF)<< 8 | (bytes[3] & 0xFF);
        byte[] data;
        if(bytes.length - 4 > 0){
            data = new byte[bytes.length - 4];
            for(int i = 0; i < data.length; i++){
                data[i] = bytes[4+i];
            }
        }
        else{
            data = new byte[0];
        }
        return new DataPacket(index, data);
    }
}
