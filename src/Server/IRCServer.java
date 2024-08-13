package Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class for the server program.
 * Holds default port number and creates a thread pool with the size of available processors
 */
public class IRCServer {

    public static final int DEFAULT_PORT = 6667;
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Entry point for server program.
     *
     * @param args First arg accepted as port number
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        ChannelManager channelManager = new ChannelManager();
        UserManager userManager = new UserManager(channelManager);
        Runnable clientHandler = new NewClientHandler(port, channelManager, userManager);
        EXECUTOR.submit(clientHandler);
    }
}
