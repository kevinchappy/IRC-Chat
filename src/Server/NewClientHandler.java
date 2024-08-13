package Server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class that handles accepting new clients connecting to server
 */
public class NewClientHandler implements Runnable {
    private final int port;
    private final ChannelManager channelManager;
    private final UserManager userManager;


    /**
     * Instantiates new NewClientHandler
     *
     * @param channelManager Channel manager
     * @param userManager    User manager
     */
    public NewClientHandler(int port, ChannelManager channelManager, UserManager userManager) {
        this.port = port;
        this.channelManager = channelManager;
        this.userManager = userManager;
    }

    /**
     * Thread for accepting new clients to server.
     * Endlessly loops and waits for new clients to connect to server.
     * New user is given default name guest.
     * user is added to ircServer.userList.
     * New Server.ClientHandler thread is spawned for specific user.
     */
    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket socket = server.accept();
                    User user = new User(socket, "guest");
                    userManager.add(user);
                    Runnable clientHandler = new ClientHandler(user, userManager, channelManager);
                    IRCServer.EXECUTOR.submit(clientHandler);

                } catch (IOException ex) {
                    System.err.println("UNABLE TO ACCEPT NEW USER");
                }
            }
        } catch (IOException ex) {
            System.err.println("UNABLE TO START SERVER");
        }
    }
}
