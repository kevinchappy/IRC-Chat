package Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class IRCServer {

    public static final int defaultPort = 6667;
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {
        ChannelHandler channelHandler = new ChannelHandler();
        UserHandler userHandler = new UserHandler(channelHandler);
        Runnable clientHandler = new NewClientHandler(channelHandler, userHandler);
        EXECUTOR.submit(clientHandler);
    }
}
