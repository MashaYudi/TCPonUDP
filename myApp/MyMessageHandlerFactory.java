package edu.spbspu.dcn.netcourse.myApp;

import edu.spbspu.dcn.netcourse.netUtils.MessageHandler;
import edu.spbspu.dcn.netcourse.netUtils.MessageHandlerFactory;

/**
 * Created by masha on 06.11.2015.
 */
public class MyMessageHandlerFactory implements MessageHandlerFactory {

    CommonChat chat = new CommonChat();
    @Override
    public MessageHandler create() {
        MessageHandler msgHandler = new MyMessageHandler(chat);
        return msgHandler;
    }
}
