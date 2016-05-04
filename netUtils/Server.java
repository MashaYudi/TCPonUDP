package edu.spbspu.dcn.netcourse.netUtils;

import edu.spbspu.dcn.netcourse.concurrentUtils.Channel;
import edu.spbspu.dcn.netcourse.concurrentUtils.Dispatcher;
import edu.spbspu.dcn.netcourse.concurrentUtils.Task;
import edu.spbspu.dcn.netcourse.concurrentUtils.ThreadPool;
import edu.spbspu.dcn.netcourse.myApp.MyMessageHandlerFactory;

/**
 * Created by masha on 11.09.15.
 */
public class Server {

    public static void main(String[] args) {



       if (args.length < 1) {
            System.out.println("Usage: port");
            return;
        }
        Channel<Task> channel = new Channel<Task>(10);
        final MessageHandlerFactory messageHandlerFactory;
        try {
            Class clMHF = Class.forName("edu.spbspu.dcn.netcourse.myApp.MyMessageHandlerFactory");
            messageHandlerFactory = (MessageHandlerFactory) clMHF.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return;
        }
        final Host host = new Host (Integer.parseInt(args[0]), Integer.parseInt(args[1]), channel, messageHandlerFactory);
        final ThreadPool threadPool = new ThreadPool(2, 6, 2);
        final Dispatcher dispatcher = new Dispatcher(channel, threadPool);
        host.start();
        dispatcher.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("beginning shutdown consequence");
                host.stop();
                System.out.println("host is down");
                dispatcher.stop();
                System.out.println("dispatcher is down");
                threadPool.stop();
                System.out.println("threadPool is down");
            }
        }));
    }
}
