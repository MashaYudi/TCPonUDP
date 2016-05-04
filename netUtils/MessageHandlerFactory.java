package edu.spbspu.dcn.netcourse.netUtils;

import edu.spbspu.dcn.netcourse.myApp.ChatMsg;
import edu.spbspu.dcn.netcourse.myApp.CommonChat;

/**
 * Created by masha on 06.11.2015.
 */
public interface MessageHandlerFactory {

    public MessageHandler create();
}
