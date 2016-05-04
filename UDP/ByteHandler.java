package edu.spbspu.dcn.netcourse.UDP;

/**
 * Created by masha on 05.12.15.
 */
public interface ByteHandler extends OnCompleteListener{
    void handle(byte[] data);

}
