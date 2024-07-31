package Server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class NewClientHandler implements Runnable {
    private final ChannelManager channelManager;
    private final UserManager userManager;
    public NewClientHandler(ChannelManager channelManager, UserManager userManager) {
        this.channelManager = channelManager;
        this.userManager = userManager;
    }

    /**
     * Thread for accepting new clients to server
     * New user is given default name guest
     * user is added to ircServer.userList
     * New Server.ClientHandler thread is spawned for specific user
     */
    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(IRCServer.defaultPort)) {
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
