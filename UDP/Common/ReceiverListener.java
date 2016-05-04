package edu.spbspu.dcn.netcourse.UDP.Common;

import edu.spbspu.dcn.netcourse.UDP.packet.DataPacket;

/**
 * Created by masha on 04.12.15.
 */
public interface ReceiverListener {
    void onReceive(DataPacket packet);
    public boolean isComplete();
}
