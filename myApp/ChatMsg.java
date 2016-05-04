package edu.spbspu.dcn.netcourse.myApp;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by masha on 20.11.2015.
 */
public class ChatMsg implements Serializable {

    public long time;
    public String nickname;
    public String message;
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    public ChatMsg (long time, String nickname, String message){
        this.time = time;
        this.nickname = nickname;
        this.message = message;
    }

    @Override
    public String toString(){
        return dateFormat.format(time) + " " + nickname + "  " + message;

    }
}
