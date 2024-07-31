package Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class IRCServer {

    public static final int defaultPort = 6667;
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {
        ChannelManager channelManager = new ChannelManager();
        UserManager userManager = new UserManager(channelManager);
        Runnable clientHandler = new NewClientHandler(channelManager, userManager);
        EXECUTOR.submit(clientHandler);
    }
}
