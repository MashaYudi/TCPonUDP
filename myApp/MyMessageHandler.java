package edu.spbspu.dcn.netcourse.myApp;

import edu.spbspu.dcn.netcourse.netUtils.MessageHandler;

/**
 * Created by masha on 06.11.2015.
 */
public class MyMessageHandler implements MessageHandler {

    CommonChat chat;
    String nickname = null;
    private final Object lock = new Object();
    public MyMessageHandler(CommonChat chat) {
        this.chat = chat;
    }
    @Override
    public String handle(String msg) {
        if (nickname == null) {
            nickname = msg;
            return "Hello," + msg + "\n";
        } else {
            if(msg.equals("veRyDiffiCULtWordToBEWRITTTenBYUSer")){
               return chat.showAll(System.currentTimeMillis());
            }
            else {
                if(msg.equals("SuperRequest")){
                    return chat.show();
                }
                synchronized (lock) {
                    chat.addMessage(System.currentTimeMillis() + " " + nickname + " " + msg);
                }
                return "";
            }
        }
    }
}
