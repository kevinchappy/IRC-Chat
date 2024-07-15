package Server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class NewClientHandler implements Runnable {
    private final IRCServer ircServer;
    public NewClientHandler(IRCServer ircServer) {
        this.ircServer = ircServer;
    }

    /**
     * Thread for accepting new clients to server
     * New user is given default name Guest
     * Server.User is added to userlist
     * New Server.ClientHandler thread is spawned for specific user
     */
    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(IRCServer.defaultPort)) {
            while (ircServer.isAlive()) {
                try {
                    Socket socket = server.accept();
                    User user = new User(socket, "Guest");
                    ircServer.getUsers().add(user);
                    Runnable clientHandler = new ClientHandler(user, ircServer);
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
