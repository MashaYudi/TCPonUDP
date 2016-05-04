package edu.spbspu.dcn.netcourse.UDP.Common;

/**
 * Created by masha on 13.01.16.
 */
public class Times {

    int id;
    long time;

    public Times(int id, long time){
        this.id = id;
        this.time = time;
    }

    public int getId(){
        return id;
    }

    public long getTime(){
        return time;
    }
}
