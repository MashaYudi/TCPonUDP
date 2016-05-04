package edu.spbspu.dcn.netcourse.myApp;

import java.util.LinkedList;

/**
 * Created by masha on 20.11.2015.
 */
public class CommonChat {
    private volatile LinkedList<ChatMsg> allMessages = new LinkedList<ChatMsg>();
    private final Object lock = new Object();
    String nickname;
    String text;
    String result = "";
    String time;
    ChatMsg tempMessage;

    public void addMessage(String msg){
        time = msg.split(" ")[0];
        nickname = msg.split(" ")[1];
        text = msg.split(" ")[2];
        tempMessage = new ChatMsg(Long.parseLong(time), nickname, text);
        synchronized (lock) {
            allMessages.add(tempMessage);
            if(allMessages.size()>15){
                for(int i = allMessages.size(); allMessages.size() > 10 ; i++){
                    allMessages.pop();
                }
            }
        }
    }

    public String show() {
        //for (int i = allMessages.size()-1, count = 0; (i>=0)&&(count<5);i--,count++) {
        for (int i = 0, count = 0; (i<allMessages.size())&&(count<5);i++,count++){
            tempMessage = allMessages.get(i);
                result = result + tempMessage.toString() + "\n";

        }
        return result;
    }
    public String showAll(long currentTime) {
        result = "";
        for (ChatMsg message : allMessages) {
           if (currentTime - message.time < 4000) {
                result = result + message.toString() + "\n";
            }
        }
        return result;
    }

}